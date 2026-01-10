package com.itvillage.renttech.dynamicform;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionOptionRepository extends JpaRepository<QuestionOption, String> {
    boolean existsByValueAndQuestionId(int value, String questionId);
}
