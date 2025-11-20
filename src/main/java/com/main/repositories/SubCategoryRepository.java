package com.main.repositories;

import com.main.entities.SubCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubCategoryRepository extends JpaRepository<SubCategoryEntity, Long> {

    // Find all subcategories for a given category
    List<SubCategoryEntity> findByCategoryId(Long categoryId);
}