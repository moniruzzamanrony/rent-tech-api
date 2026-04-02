package com.itvillage.renttech.rentalpost;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface RentalPostRepository extends JpaRepository<RentalPost, String> {
    @EntityGraph(attributePaths = {"category", "owner"})
    Page<RentalPost> findByOwner_IdOrderByModifiedDateDesc(String ownerId, Pageable pageable);

    @Query(value = """
SELECT
    r.id,
    r.latitude,
    r.longitude,
    r.valid,
    JSON_OBJECTAGG(
        SUBSTRING_INDEX(q.dynamic_form_question_id, '_', 3),
        v.answer
    ) AS answers
FROM rental_post r
JOIN user_answer_dynamic_form_question q
  ON q.rental_post_id = r.id
JOIN user_answer_values v
  ON v.user_answer_question_id = q.id
WHERE r.category_id = :categoryId
  AND q.dynamic_form_question_id LIKE 'SYS_%'
GROUP BY r.id, r.latitude, r.longitude, r.valid""", nativeQuery = true)
    List<RentalMapMarkerProjection> findAllByCategoryId(@Param("categoryId") String categoryId);

    @Query("""
       SELECT DISTINCT r
       FROM RentalPost r
       JOIN r.interestedPeople u
       WHERE u.id = :userId
       """)
    Page<RentalPost> findAllByInterestedUserId(@Param("userId") String userId, Pageable pageable);

    List<RentalPost> findAllByValidTrueAndExpiryDateBefore(ZonedDateTime now);
}
