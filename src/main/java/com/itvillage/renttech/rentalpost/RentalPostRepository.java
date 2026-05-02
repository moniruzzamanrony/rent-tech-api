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
import java.util.Optional;

@Repository
public interface RentalPostRepository extends JpaRepository<RentalPost, String> {
    @Query("""
            SELECT DISTINCT
                r.id as id,
                r.createdDate as createdDate,
                u.mobileNo as mobileNo,
                c.name as categoryName,
                c.iconUrl as categoryIconUrl,
                ans.answer as title,
                SIZE(r.interestedPeople) as interestedPeopleCount
            FROM RentalPost r
            JOIN r.owner u
            JOIN r.category c
            JOIN r.formQuestionsAnswer qa
            JOIN qa.dynamicFormQuestion df
            JOIN qa.answers ans
            WHERE df.id LIKE 'SYS_TITLE%'
            AND r.owner.id = :ownerId
            ORDER BY r.createdDate DESC
            """)
    Page<RentalPostListResponse> findRentalPostListByOwnerId(
            @Param("ownerId") String ownerId,
            Pageable pageable
    );

    @Query("""
            SELECT DISTINCT r FROM RentalPost r
            JOIN FETCH r.formQuestionsAnswer fqa
            JOIN FETCH fqa.dynamicFormQuestion dq
            JOIN FETCH fqa.answers
            WHERE r.category.id = :categoryId
            AND r.valid = true
            AND dq.purposeType = com.itvillage.renttech.dynamicform.PurposeType.SPECIFICATION
            """)
    List<RentalPost> findAllByCategoryIdForMap(@Param("categoryId") String categoryId);

    @Query("""
            SELECT DISTINCT
                r.id as id,
                u.mobileNo as mobileNo,
                c.name as categoryName,
                c.iconUrl as categoryIconUrl,
                ans.answer as title,
                SIZE(r.interestedPeople) as interestedPeopleCount
            FROM RentalPost r
            JOIN r.owner u
            JOIN r.category c
            JOIN r.formQuestionsAnswer qa
            JOIN qa.dynamicFormQuestion df
            JOIN qa.answers ans
            JOIN r.interestedPeople ip
            WHERE df.id LIKE 'SYS_TITLE%'
            AND ip.id = :userId
            """)
    Page<RentalPostListResponse> findAllByInterestedUserId(
            @Param("userId") String userId,
            Pageable pageable
    );

    List<RentalPost> findAllByValidTrueAndExpiryDateBefore(ZonedDateTime now);

    @Query("""
                SELECT DISTINCT r
                FROM RentalPost r
                LEFT JOIN FETCH r.owner
                LEFT JOIN FETCH r.category
                LEFT JOIN FETCH r.rentalPostFiles
                LEFT JOIN FETCH r.formQuestionsAnswer fqa
                LEFT JOIN FETCH fqa.dynamicFormQuestion dq
                LEFT JOIN FETCH dq.defaultOptions
                LEFT JOIN FETCH fqa.answers
                LEFT JOIN FETCH r.interestedPeople
                WHERE r.id = :rentalId
            """)
    Optional<RentalPost> findFullById(@Param("rentalId") String rentalId);
}
