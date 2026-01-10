package com.itvillage.renttech.dynamicform;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DynamicFormQuestionRepository extends JpaRepository<DynamicFormQuestion, String> {
    List<DynamicFormQuestion> findAllByCategoryId(String categoryId);
}
