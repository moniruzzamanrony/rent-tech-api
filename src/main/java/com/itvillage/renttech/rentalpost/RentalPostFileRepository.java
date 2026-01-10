package com.itvillage.renttech.rentalpost;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalPostFileRepository extends JpaRepository<RentalPostFile, String> {

}
