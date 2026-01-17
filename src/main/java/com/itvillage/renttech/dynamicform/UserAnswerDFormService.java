package com.itvillage.renttech.dynamicform;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserAnswerDFormService {

    private final UserAnswerDFormQuestionRepository userAnswerDFormQuestionRepository;

    public boolean hasAnswerByQsId(String questionId) {
        return userAnswerDFormQuestionRepository.existsByDynamicFormQuestionId(questionId);
    }
}
