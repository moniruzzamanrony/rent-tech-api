package com.itvillage.renttech.dynamicform;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserAnswerDFormService {

    private final UserAnswerDFormQuestionRepository userAnswerDFormQuestionRepository;

    public void saveAnswers(List<UserAnswerDFormQuestion> userAnswerDFormQuestions) {
        userAnswerDFormQuestionRepository.saveAll(userAnswerDFormQuestions);
    }
}
