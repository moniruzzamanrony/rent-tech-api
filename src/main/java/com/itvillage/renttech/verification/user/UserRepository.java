package com.itvillage.renttech.verification.user;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

  Optional<User> findByMobileNo(String phoneNo);

  Optional<User> findById(String userId);

  boolean existsByMobileNo(String phoneNo);

  List<User> findByMobileNoIn(Set<String> mobileNumbers);

  List<User> findByIdIn(Set<String> ids);

  Page<User> findAllByRole(Role role, Pageable pageable);

  int countByRole(Role role);

  List<User> findAllByIdIn(Set<String> strings);

  boolean existsByIdAndUserPackagesIsNotEmpty(String userId);
}
