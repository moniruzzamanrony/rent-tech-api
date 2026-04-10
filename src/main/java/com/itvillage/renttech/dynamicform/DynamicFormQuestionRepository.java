package com.itvillage.renttech.dynamicform;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface DynamicFormQuestionRepository extends JpaRepository<DynamicFormQuestion, String> {

    @Query("""
        SELECT DISTINCT q
        FROM DynamicFormQuestion q
        LEFT JOIN FETCH q.defaultOptions
        WHERE q.category.id = :categoryId AND q.deleted = false
        ORDER BY q.position ASC
        """)
    List<DynamicFormQuestion> findAllByCategoryIdOrderByPositionAsc(@Param("categoryId") String categoryId);

    @Query("""
        SELECT q
        FROM DynamicFormQuestion q
        WHERE q.id IN :questionIds
        """)
    List<DynamicFormQuestion> findAllByIdIn(@Param("questionIds") Set<String> questionIds);

    @Query("""
    SELECT CASE WHEN COUNT(q) > 0 THEN true ELSE false END
    FROM DynamicFormQuestion q
    WHERE q.category.id = :catId
    AND q.purposeType = :purposeType
    """)
    boolean existsByCategoryIdAndPurposeType(
            @Param("catId") String catId,
            @Param("purposeType") PurposeType purposeType
    );

    boolean existsByCategoryIdAndPurposeTypeAndIdNot(String categoryId, PurposeType purposeType, String id);
}
