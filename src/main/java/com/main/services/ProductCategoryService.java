package com.main.services;

import com.main.dtos.ProductCategoryDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductCategoryService {

    ProductCategoryDTO createCategory(ProductCategoryDTO dto);

    ProductCategoryDTO getCategoryById(Long id);

    List<ProductCategoryDTO> getAllCategories();

    ProductCategoryDTO updateCategory(Long id, ProductCategoryDTO dto);

    boolean deleteCategory(Long id);
}