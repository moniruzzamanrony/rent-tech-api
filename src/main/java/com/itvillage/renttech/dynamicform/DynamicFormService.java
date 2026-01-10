package com.itvillage.renttech.dynamicform;


import com.itvillage.renttech.base.expection.MagicException;
import com.itvillage.renttech.base.utils.ConverterUtils;
import com.itvillage.renttech.category.Category;
import com.itvillage.renttech.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class DynamicFormService {

    private final DynamicFormQuestionRepository dynamicFormQuestionRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final UserAnswerDFormService userAnswerDFormService;
    private final CategoryService categoryService;


    public DynamicFormQuestionResponse createDynamicFormQuestion(DynamicFormQuestionRequest dynamicFormQuestionRequest) {
        DynamicFormQuestion dynamicFormQuestion = new DynamicFormQuestion();
        BeanUtils.copyProperties(dynamicFormQuestionRequest, dynamicFormQuestion);
        Category category = categoryService.getById(dynamicFormQuestionRequest.getCategoryId());
        dynamicFormQuestion.setCategory(category);

        dynamicFormQuestion = dynamicFormQuestionRepository.save(dynamicFormQuestion);

        return ConverterUtils.convert(dynamicFormQuestion);

    }

    public DynamicFormQuestionResponse addOptionsInQuestion(String questionId, QuestionOptionRequest request) {
        if (questionOptionRepository.existsByValueAndQuestionId(request.getValue(), questionId))
            throw new MagicException.AlreadyExistsException("Question option value already exists");
        Optional<DynamicFormQuestion> dynamicFormQuestion = dynamicFormQuestionRepository.findById(questionId);
        if (dynamicFormQuestion.isEmpty()) {
            throw new MagicException.NotFoundException("Question not found");
        }
        QuestionOption questionOption = new QuestionOption();
        BeanUtils.copyProperties(request, questionOption);
        questionOption.setQuestion(dynamicFormQuestion.get());
        questionOption = questionOptionRepository.save(questionOption);
        dynamicFormQuestion.get().getDefaultOptions().add(questionOption);
        return ConverterUtils.convert(dynamicFormQuestionRepository.save(dynamicFormQuestion.get()));
    }

    public void deleteQuestion(String questionId) {
        Optional<DynamicFormQuestion> dynamicFormQuestion = dynamicFormQuestionRepository.findById(questionId);
        if (dynamicFormQuestion.isEmpty()) {
            throw new MagicException.NotFoundException("Question not found");
        }
        dynamicFormQuestionRepository.delete(dynamicFormQuestion.get());

    }

    public void deleteOptionOfQuestion(String questionId, String optionId) {
        Optional<DynamicFormQuestion> dynamicFormQuestion = dynamicFormQuestionRepository.findById(questionId);
        if (dynamicFormQuestion.isEmpty()) {
            throw new MagicException.NotFoundException("Question not found");
        }
        Optional<QuestionOption> questionOption = questionOptionRepository.findById(optionId);
        if (questionOption.isEmpty()) {
            throw new MagicException.NotFoundException("Option not found");
        }
        dynamicFormQuestion.get().getDefaultOptions().remove(questionOption.get());
        dynamicFormQuestionRepository.save(dynamicFormQuestion.get());
        questionOptionRepository.delete(questionOption.get());
    }


    public void saveAnswers(List<UserAnswerDFormQuestion> userAnswerDFormQuestions) {
        userAnswerDFormService.saveAnswers(userAnswerDFormQuestions);
    }

    public List<DynamicFormQuestionResponse> getFormBycCategoryId(String categoryId) {
        List<DynamicFormQuestion>  dynamicFormQuestions = dynamicFormQuestionRepository.findAllByCategoryId(categoryId);
        return dynamicFormQuestions.stream().map(ConverterUtils::convert).toList();
    }

    public DynamicFormQuestion getDFQuestionById(String questionId) {
        return dynamicFormQuestionRepository.findById(questionId).orElseThrow(() -> new MagicException.NotFoundException("Question not found"));
    }


    public List<DynamicFormQuestion> getByIds(Set<String> questionIds) {
        return dynamicFormQuestionRepository.findAllByIdIn(questionIds);

    }
}
