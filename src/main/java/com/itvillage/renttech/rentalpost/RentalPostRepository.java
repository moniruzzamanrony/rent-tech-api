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

    @Query("SELECT r FROM RentalPost r JOIN FETCH r.category c WHERE c.id = :categoryId")
    List<RentalPost> findAllByCategoryId(@Param("categoryId") String categoryId);

    @Query("""
           SELECT r 
           FROM RentalPost r
           JOIN r.interestedPeople u
           WHERE u.id = :userId
           """)
    List<RentalPost> findAllByInterestedUserId(@Param("userId") String userId);

    List<RentalPost> findAllByValidTrueAndExpiryDateBefore(ZonedDateTime now);
}
