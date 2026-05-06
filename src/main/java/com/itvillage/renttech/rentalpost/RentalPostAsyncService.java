package com.itvillage.renttech.rentalpost;

import com.itvillage.renttech.base.expection.MagicException;
import com.itvillage.renttech.dynamicform.DynamicFormQuestion;
import com.itvillage.renttech.dynamicform.DynamicFormService;
import com.itvillage.renttech.dynamicform.PurposeType;
import com.itvillage.renttech.dynamicform.QuestionOption;
import com.itvillage.renttech.dynamicform.QuestionOptionRepository;
import com.itvillage.renttech.dynamicform.UserAnswerDFormQuestion;
import com.itvillage.renttech.dynamicform.UserAnswerDFormQuestionRequest;
import com.itvillage.renttech.dynamicform.UserAnswerRequest;
import com.itvillage.renttech.dynamicform.UserAnswerValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RentalPostAsyncService {

    private final RentalPostRepository rentalPostRepository;
    private final DynamicFormService dynamicFormService;
    private final QuestionOptionRepository questionOptionRepository;

    @Async("rentalPostExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void finishProcessing(String rentalPostId, List<UserAnswerDFormQuestionRequest> formAnswers) {
        RentalPost rentalPost = rentalPostRepository.findFullById(rentalPostId).orElse(null);
        if (rentalPost == null) {
            log.warn("Rental post {} not found for async processing", rentalPostId);
            return;
        }

        try {
            attachFormAnswers(rentalPost, formAnswers);
            rentalPost.setProcessingStatus(ProcessingStatus.READY);
            rentalPostRepository.save(rentalPost);
            log.info("Rental post {} processing READY", rentalPostId);
        } catch (Exception e) {
            log.error("Async processing failed for rental post {}", rentalPostId, e);
            markFailed(rentalPostId);
        }
    }

    @Async("rentalPostExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void finishUpdateProcessing(String rentalPostId, List<UserAnswerDFormQuestionRequest> formAnswers) {
        RentalPost rentalPost = rentalPostRepository.findFullById(rentalPostId).orElse(null);
        if (rentalPost == null) {
            log.warn("Rental post {} not found for async update processing", rentalPostId);
            return;
        }

        try {
            replaceFormAnswers(rentalPost, formAnswers);
            rentalPost.setProcessingStatus(ProcessingStatus.READY);
            rentalPostRepository.save(rentalPost);
            log.info("Rental post {} update processing READY", rentalPostId);
        } catch (Exception e) {
            log.error("Async update processing failed for rental post {}", rentalPostId, e);
            markFailed(rentalPostId);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(String rentalPostId) {
        rentalPostRepository.findById(rentalPostId).ifPresent(p -> {
            p.setProcessingStatus(ProcessingStatus.FAILED);
            rentalPostRepository.save(p);
        });
    }

    private void attachFormAnswers(RentalPost rentalPost, List<UserAnswerDFormQuestionRequest> formAnswers) {
        if (formAnswers == null || formAnswers.isEmpty()) {
            return;
        }

        Set<String> questionIds = formAnswers.stream()
                .map(UserAnswerDFormQuestionRequest::getDynamicFormQuestionId)
                .collect(Collectors.toSet());

        Map<String, DynamicFormQuestion> questionMap = dynamicFormService.getByIds(questionIds).stream()
                .collect(Collectors.toMap(DynamicFormQuestion::getId, q -> q));

        Map<String, QuestionOption> optionMap = loadOptionMap(formAnswers);

        Set<UserAnswerDFormQuestion> answerEntities = new HashSet<>();
        for (UserAnswerDFormQuestionRequest q : formAnswers) {
            DynamicFormQuestion question = questionMap.get(q.getDynamicFormQuestionId());
            if (question == null) {
                throw new MagicException.NotFoundException("Dynamic question not found: " + q.getDynamicFormQuestionId());
            }

            UserAnswerDFormQuestion answerEntity = new UserAnswerDFormQuestion();
            answerEntity.setDynamicFormQuestion(question);

            if (q.getAnswers() != null && !q.getAnswers().isEmpty()) {
                Set<UserAnswerValue> values = q.getAnswers().stream().map(req -> {
                    UserAnswerValue v = new UserAnswerValue();
                    String optionId = req.getOptionId();
                    if (optionId != null && !optionId.isBlank()) {
                        v.setQuestionOption(optionMap.get(optionId));
                    }
                    v.setAnswer(req.getValue());
                    v.setQuestion(answerEntity);
                    return v;
                }).collect(Collectors.toSet());
                answerEntity.setAnswers(values);
            }

            answerEntities.add(answerEntity);
        }

        rentalPost.getFormQuestionsAnswer().addAll(answerEntities);

        String specIds = answerEntities.stream()
                .filter(a -> PurposeType.SPECIFICATION.equals(a.getDynamicFormQuestion().getPurposeType()))
                .sorted(Comparator.comparingInt(a -> a.getDynamicFormQuestion().getPosition()))
                .limit(3)
                .map(a -> a.getDynamicFormQuestion().getId())
                .collect(Collectors.joining(","));
        rentalPost.setFirst3SpecificationsIds(specIds.isEmpty() ? null : specIds);
    }

    private void replaceFormAnswers(RentalPost rentalPost, List<UserAnswerDFormQuestionRequest> formAnswers) {
        if (formAnswers == null || formAnswers.isEmpty()) {
            return;
        }

        Set<String> questionIds = formAnswers.stream()
                .map(UserAnswerDFormQuestionRequest::getDynamicFormQuestionId)
                .collect(Collectors.toSet());

        Map<String, DynamicFormQuestion> questionMap = dynamicFormService.getByIds(questionIds).stream()
                .collect(Collectors.toMap(DynamicFormQuestion::getId, q -> q));

        Map<String, QuestionOption> optionMap = loadOptionMap(formAnswers);

        // Reuse existing parent answer entities by question id so their PKs stay stable.
        Map<String, UserAnswerDFormQuestion> existingAnswerMap = rentalPost.getFormQuestionsAnswer().stream()
                .collect(Collectors.toMap(ans -> ans.getDynamicFormQuestion().getId(), ans -> ans));

        List<UserAnswerDFormQuestion> updatedAnswers = new ArrayList<>();
        for (UserAnswerDFormQuestionRequest req : formAnswers) {
            DynamicFormQuestion question = questionMap.get(req.getDynamicFormQuestionId());
            if (question == null) {
                throw new MagicException.NotFoundException(
                        "Dynamic question not found: " + req.getDynamicFormQuestionId());
            }

            UserAnswerDFormQuestion answer = existingAnswerMap.get(req.getDynamicFormQuestionId());
            if (answer == null) {
                answer = new UserAnswerDFormQuestion();
                answer.setDynamicFormQuestion(question);
            }

            // orphanRemoval cleans the old child rows
            answer.getAnswers().clear();
            if (req.getAnswers() != null) {
                for (UserAnswerRequest uar : req.getAnswers()) {
                    UserAnswerValue v = new UserAnswerValue();
                    v.setAnswer(uar.getValue());
                    String optionId = uar.getOptionId();
                    if (optionId != null && !optionId.isBlank()) {
                        v.setQuestionOption(optionMap.get(optionId));
                    }
                    v.setQuestion(answer);
                    answer.getAnswers().add(v);
                }
            }
            updatedAnswers.add(answer);
        }

        rentalPost.getFormQuestionsAnswer().clear();
        rentalPost.getFormQuestionsAnswer().addAll(updatedAnswers);

        String specIds = updatedAnswers.stream()
                .filter(a -> PurposeType.SPECIFICATION.equals(a.getDynamicFormQuestion().getPurposeType()))
                .sorted(Comparator.comparingInt(a -> a.getDynamicFormQuestion().getPosition()))
                .limit(3)
                .map(a -> a.getDynamicFormQuestion().getId())
                .collect(Collectors.joining(","));
        rentalPost.setFirst3SpecificationsIds(specIds.isEmpty() ? null : specIds);
    }

    private Map<String, QuestionOption> loadOptionMap(List<UserAnswerDFormQuestionRequest> formAnswers) {
        Set<String> optionIds = formAnswers.stream()
                .filter(q -> q.getAnswers() != null)
                .flatMap(q -> q.getAnswers().stream())
                .map(UserAnswerRequest::getOptionId)
                .filter(id -> id != null && !id.isBlank())
                .collect(Collectors.toSet());

        if (optionIds.isEmpty()) {
            return Map.of();
        }
        return questionOptionRepository.findAllById(optionIds).stream()
                .collect(Collectors.toMap(QuestionOption::getId, o -> o));
    }
}
