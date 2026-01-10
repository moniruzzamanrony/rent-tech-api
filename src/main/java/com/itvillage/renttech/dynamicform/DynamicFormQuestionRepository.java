package com.itvillage.renttech.dynamicform;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface DynamicFormQuestionRepository extends JpaRepository<DynamicFormQuestion, String> {

    List<DynamicFormQuestion> findAllByCategoryIdOrderByPositionAsc(String categoryId);

    List<DynamicFormQuestion> findAllByIdIn(Set<String> questionIds);
}
