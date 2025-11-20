package com.main.services;

import com.main.dtos.SubCategoryDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SubCategoryService {

    SubCategoryDTO createSubCategory(SubCategoryDTO dto);

    SubCategoryDTO getSubCategoryById(Long id);

    List<SubCategoryDTO> getAllSubCategories();

    List<SubCategoryDTO> getSubCategoriesByCategory(Long categoryId);

    SubCategoryDTO updateSubCategory(Long id, SubCategoryDTO dto);

    boolean deleteSubCategory(Long id);
}