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
import java.util.Set;

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

    public void deleteQuestion(String questionId) {

        if (hasAnyAnswer(questionId))
            throw new MagicException.NotPermittedException("Question has answers");

        DynamicFormQuestion dynamicFormQuestion = dynamicFormQuestionRepository.findById(questionId)
                .orElseThrow(() -> new MagicException.NotFoundException("Question not found"));

        dynamicFormQuestionRepository.delete(dynamicFormQuestion);
    }

    private boolean hasAnyAnswer(String questionId) {
        return userAnswerDFormService.hasAnswerByQsId(questionId);
    }

    public void deleteOptionOfQuestion(String questionId, String optionId) {

        if (hasAnyAnswer(questionId))
            throw new MagicException.NotPermittedException("Question has answers");

        DynamicFormQuestion dynamicFormQuestion = dynamicFormQuestionRepository.findById(questionId)
                .orElseThrow(() -> new MagicException.NotFoundException("Question not found"));

        QuestionOption questionOption = questionOptionRepository.findById(optionId)
                .orElseThrow(() -> new MagicException.NotFoundException("Option not found"));

        dynamicFormQuestion.getDefaultOptions().remove(questionOption);

        dynamicFormQuestionRepository.save(dynamicFormQuestion);
        questionOptionRepository.delete(questionOption);
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
}
