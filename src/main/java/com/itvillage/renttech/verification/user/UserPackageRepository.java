package com.itvillage.renttech.verification.user;


import com.itvillage.renttech.rentalpost.RentalPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserPackageRepository extends JpaRepository<UserPackage, String> {
  List<UserPackage> findAllByValidTrueAndExpiryDateBefore(ZonedDateTime now);
}
