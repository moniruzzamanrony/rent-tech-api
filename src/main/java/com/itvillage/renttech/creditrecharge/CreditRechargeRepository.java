package com.itvillage.renttech.creditrecharge;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CreditRechargeRepository extends JpaRepository<CreditRecharge, String> {
    Optional<CreditRecharge> findTopByOrderByCreatedDateDesc();
}
