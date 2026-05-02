package com.itvillage.renttech.verification.user;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
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
    AND (up IS NULL OR up.valid = true)
""")
  Optional<User> findFullUser(@Param("userId") String userId);


  Page<User> findAllByRole(Role role, Pageable pageable);


  List<User> findAllByIdIn(Set<String> strings);

  boolean existsByIdAndUserPackagesIsNotEmpty(String userId);

  @Query(value = """
      SELECT
          u.id,
          u.name,
          u.mobile_no       AS mobileNo,
          u.gender,
          u.nid_number      AS nidNumber,
          u.present_address AS presentAddress,
          u.current_coins   AS currentCoins,
          u.profile_pic_url AS profilePicUrl,
          u.profession,
          u.university_name AS universityName,
          u.created_date    AS createdDate,
          (SELECT COUNT(up.id)
           FROM user_package up
           WHERE up.user_id = u.id AND up.is_deleted = false)                 AS countTotalPurchaseSearchingPackages,
          (SELECT COALESCE(SUM(rp.price_in_coins), 0)
           FROM user_package up
           JOIN rent_package rp ON rp.id = up.rent_package_id
           WHERE up.user_id = u.id AND up.is_deleted = false)                 AS totalSpendAmount,
          (SELECT COUNT(post.id)
           FROM rental_post post
           WHERE post.user_id = u.id AND post.is_deleted = false)             AS countTotalPost
      FROM users u
      WHERE u.is_deleted = false
        AND (COALESCE(:mobileNo, '') = '' OR u.mobile_no LIKE CONCAT('%', :mobileNo, '%'))
      """,
      countQuery = """
          SELECT COUNT(u.id) FROM users u
          WHERE u.is_deleted = false
            AND (COALESCE(:mobileNo, '') = '' OR u.mobile_no LIKE CONCAT('%', :mobileNo, '%'))
          """,
      nativeQuery = true)
  Page<UserAdminProjection> findAdminUsers(@Param("mobileNo") String mobileNo, Pageable pageable);
}
