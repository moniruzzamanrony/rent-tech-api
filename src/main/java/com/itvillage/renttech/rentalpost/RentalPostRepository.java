package com.itvillage.renttech.rentalpost;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalPostRepository extends JpaRepository<RentalPost, String> {

    List<RentalPost> findAllByOwnerId(String currentUserId);

    @Query("SELECT r FROM RentalPost r JOIN FETCH r.category c WHERE c.id = :categoryId")
    List<RentalPost> findAllByCategoryId(@Param("categoryId") String categoryId);

    @Query("""
           SELECT r 
           FROM RentalPost r
           JOIN r.interestedPeople u
           WHERE u.id = :userId
           """)
    List<RentalPost> findAllByInterestedUserId(@Param("userId") String userId);
}
