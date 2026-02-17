package com.itvillage.renttech.category;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, String> {

    List<Category> findAllByIdIn(List<String> ids);

    List<Category> findByActiveTrue();
}
