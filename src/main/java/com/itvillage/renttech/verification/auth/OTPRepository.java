package com.itvillage.renttech.verification.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OTPRepository extends JpaRepository<OTP, String> {

  Optional<OTP> findByPhoneNoAndOtpCodeAndIsValid(String phoneNo, String code, boolean isValid);

  Optional<OTP> findByPhoneNoAndIsValid(String phoneNo, boolean isValid);

  Optional<OTP> findByPhoneNo(String phoneNo);

  boolean existsByPhoneNo(String phoneNo);

  void deleteAllByPhoneNo(String phoneNo);
}
