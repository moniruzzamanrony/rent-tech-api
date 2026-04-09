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

  @Query("""
    SELECT DISTINCT u
    FROM User u
    LEFT JOIN FETCH u.userPackages up
    LEFT JOIN FETCH up.rentPackage
    WHERE u.mobileNo = :mobileNo
""")
  Optional<User> findByMobileNo(@Param("mobileNo") String mobileNo);


  @Query("""
    SELECT DISTINCT u 
    FROM User u
    LEFT JOIN FETCH u.userPackages up
    LEFT JOIN FETCH up.rentPackage
    WHERE u.id = :userId
""")
  Optional<User> findFullUser(@Param("userId") String userId);
  Optional<User> findById(@Param("userId") String userId);

  boolean existsByMobileNo(String phoneNo);

  List<User> findByMobileNoIn(Set<String> mobileNumbers);

  List<User> findByIdIn(Set<String> ids);

  Page<User> findAllByRole(Role role, Pageable pageable);

  int countByRole(Role role);

  List<User> findAllByIdIn(Set<String> strings);

  boolean existsByIdAndUserPackagesIsNotEmpty(String userId);
}
