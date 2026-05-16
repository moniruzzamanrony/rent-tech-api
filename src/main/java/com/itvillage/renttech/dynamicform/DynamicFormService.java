package com.itvillage.renttech.dynamicform;

import com.itvillage.renttech.base.expection.MagicException;
import com.itvillage.renttech.base.modules.s3.SpaceService;
import com.itvillage.renttech.base.utils.ConverterUtils;
import com.itvillage.renttech.category.Category;
import com.itvillage.renttech.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DynamicFormService {

    private final DynamicFormQuestionRepository dynamicFormQuestionRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final UserAnswerDFormService userAnswerDFormService;

    // ✅ REPLACED CategoryService WITH CategoryRepository
    private final CategoryRepository categoryRepository;

    private final SpaceService spaceService;

    public DynamicFormQuestionResponse createDynamicFormQuestion(
            DynamicFormQuestionRequest dynamicFormQuestionRequest,
            MultipartFile file
    ) {

        if(dynamicFormQuestionRequest.getPurposeType().equals(PurposeType.AMENITIES))
        {
            if(dynamicFormQuestionRepository.existsByCategoryIdAndPurposeType(dynamicFormQuestionRequest.getCategoryId(), PurposeType.AMENITIES)){
                throw new MagicException.AlreadyExistsException("Amenities already exists");
        }
        }
        DynamicFormQuestion dynamicFormQuestion = new DynamicFormQuestion();
        BeanUtils.copyProperties(dynamicFormQuestionRequest, dynamicFormQuestion);

        // ✅ Fetch category directly from repository (NO service call)
        Category category = categoryRepository.findById(
                dynamicFormQuestionRequest.getCategoryId()
        ).orElseThrow(() ->
                new MagicException.NotFoundException("Category not found")
        );

        dynamicFormQuestion.setCategory(category);

        if (file != null && !file.isEmpty()) {
            String url = spaceService.uploadFile(file);
            dynamicFormQuestion.setAnswerViewIconUrl(url);
        }

        dynamicFormQuestion = dynamicFormQuestionRepository.save(dynamicFormQuestion);

        return ConverterUtils.convert(dynamicFormQuestion);
    }

    public DynamicFormQuestionResponse addOptionsInQuestion(String questionId, QuestionOptionRequest request, MultipartFile file) {

        if (questionOptionRepository.existsByValueAndQuestionId(request.getValue(), questionId))
            throw new MagicException.AlreadyExistsException("Question option value already exists");

        DynamicFormQuestion dynamicFormQuestion = dynamicFormQuestionRepository.findById(questionId)
                .orElseThrow(() -> new MagicException.NotFoundException("Question not found"));

        QuestionOption questionOption = new QuestionOption();
        BeanUtils.copyProperties(request, questionOption);
        questionOption.setQuestion(dynamicFormQuestion);
        if (file != null && !file.isEmpty()) {
            String url = spaceService.uploadFile(file);
            questionOption.setIconUrl(url);
        }
        questionOption = questionOptionRepository.save(questionOption);

        dynamicFormQuestion.getDefaultOptions().add(questionOption);

        return ConverterUtils.convert(dynamicFormQuestionRepository.save(dynamicFormQuestion));
    }

    public DynamicFormQuestionResponse updateOptionInQuestion(
            String questionId,
            String optionId,
            QuestionOptionRequest request,
            MultipartFile file
    ) {

        // 1. Fetch question
        DynamicFormQuestion question = dynamicFormQuestionRepository.findById(questionId)
                .orElseThrow(() ->
                        new MagicException.NotFoundException("Question not found")
                );

        // 2. Fetch option
        QuestionOption option = questionOptionRepository.findById(optionId)
                .orElseThrow(() ->
                        new MagicException.NotFoundException("Option not found")
                );

        // 3. Validate option belongs to question (IMPORTANT)
        if (!option.getQuestion().getId().equals(questionId)) {
            throw new MagicException.BadRequestException("Option does not belong to this question");
        }

        // 4. Unique check (exclude current option)
        boolean exists = questionOptionRepository
                .existsByValueAndQuestionIdAndIdNot(
                        request.getValue(),
                        questionId,
                        optionId
                );

        if (exists) {
            throw new MagicException.AlreadyExistsException("Question option value already exists");
        }

        // 5. Update fields (avoid overriding ID, relation, file)
        BeanUtils.copyProperties(request, option, "id", "question", "iconUrl");

        // 6. File handling (replace only if new file comes)
        if (file != null && !file.isEmpty()) {
            String url = spaceService.uploadFile(file);
            option.setIconUrl(url);
        }

        // 7. Save option (no need to re-add to list)
        questionOptionRepository.save(option);

        // 8. Return updated question
        return ConverterUtils.convert(question);
    }

    public void deleteQuestion(String questionId) {
        DynamicFormQuestion dynamicFormQuestion = dynamicFormQuestionRepository.findById(questionId)
                .orElseThrow(() -> new MagicException.NotFoundException("Question not found"));
        dynamicFormQuestion.setDelete(true);
        dynamicFormQuestionRepository.save(dynamicFormQuestion);
    }

    private boolean hasAnyAnswer(String questionId) {
        return userAnswerDFormService.hasAnswerByQsId(questionId);
    }

    public void deleteOptionOfQuestion(String questionId, String optionId) {

        if (hasAnyAnswer(questionId))
            throw new MagicException.NotPermittedException("Question has answers");

        QuestionOption questionOption = questionOptionRepository.findById(optionId)
                .orElseThrow(() -> new MagicException.NotFoundException("Option not found"));

        questionOption.setDelete(true);
        questionOptionRepository.save(questionOption);
    }

    public List<DynamicFormQuestionResponse> getFormBycCategoryId(String categoryId) {

        List<DynamicFormQuestion> dynamicFormQuestions =
                dynamicFormQuestionRepository
                        .findAllByCategoryIdOrderByPositionAsc(categoryId);

        return dynamicFormQuestions.stream()
                .map(ConverterUtils::convert)
                .toList();
    }

    public List<DynamicFormQuestion> getByIds(Set<String> questionIds) {
        return dynamicFormQuestionRepository.findAllByIdIn(questionIds);
    }

    public List<DynamicFormQuestionResponse> updatePositionOfDynamicQs(
            List<DynamicFormQuestionRequest> requests) {

        // 1. Extract all IDs
        List<String> ids = requests.stream()
                .map(DynamicFormQuestionRequest::getId)
                .toList();

        // 2. Fetch all in ONE query
        List<DynamicFormQuestion> questions = dynamicFormQuestionRepository.findAllById(ids);

        // 3. Convert list → map for O(1) lookup
        Map<String, DynamicFormQuestion> questionMap = questions.stream()
                .collect(Collectors.toMap(DynamicFormQuestion::getId, Function.identity()));

        // 4. Update positions
        for (DynamicFormQuestionRequest request : requests) {
            DynamicFormQuestion question = questionMap.get(request.getId());

            if (question == null) {
                throw new MagicException.NotFoundException("Question not found: " + request.getId());
            }

            question.setPosition(request.getPosition());
        }

        // 5. Save all (batch)
        List<DynamicFormQuestion> updated = dynamicFormQuestionRepository.saveAll(questions);

        // 6. Convert response
        return updated.stream()
                .map(ConverterUtils::convert)
                .toList();
    }

    public DynamicFormQuestionResponse updateDynamicFormQuestion(
            String id,
            DynamicFormQuestionRequest request,
            MultipartFile file
    ) {

        // 1. Fetch existing question
        DynamicFormQuestion question = dynamicFormQuestionRepository.findById(id)
                .orElseThrow(() ->
                        new MagicException.NotFoundException("Question not found")
                );

        // 2. Validation (exclude current ID)
        if (request.getPurposeType() == PurposeType.AMENITIES) {
            boolean exists = dynamicFormQuestionRepository
                    .existsByCategoryIdAndPurposeTypeAndIdNot(
                            request.getCategoryId(),
                            PurposeType.AMENITIES,
                            id
                    );

            if (exists) {
                throw new MagicException.AlreadyExistsException("Amenities already exists");
            }
        }

        // 3. Update basic fields (avoid overwriting ID)
        BeanUtils.copyProperties(request, question, "id", "category", "answerViewIconUrl");

        // 4. Update category (if changed)
        if (request.getCategoryId() != null &&
                (question.getCategory() == null ||
                        !question.getCategory().getId().equals(request.getCategoryId()))) {

            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() ->
                            new MagicException.NotFoundException("Category not found")
                    );

            question.setCategory(category);
        }

        // 5. File handling (replace only if new file comes)
        if (file != null && !file.isEmpty()) {
            String url = spaceService.uploadFile(file);
            question.setAnswerViewIconUrl(url);
        }

        // 6. Save
        DynamicFormQuestion updated = dynamicFormQuestionRepository.save(question);

        return ConverterUtils.convert(updated);
    }

}
