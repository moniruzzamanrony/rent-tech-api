package com.itvillage.renttech.dynamicform;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAnswerDFormQuestionRepository extends JpaRepository<UserAnswerDFormQuestion, String> {
}
