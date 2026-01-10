package com.itvillage.renttech.rentalpost;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalPostRepository extends JpaRepository<RentalPost, String> {

    List<RentalPost> findAllByOwnerId(String currentUserId);
}
