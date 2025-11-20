package com.main.controllers;

import com.main.dtos.ProductCategoryDTO;
import com.main.services.ProductCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/product-categories")
public class ProductCategoryController {

    @Autowired
    private ProductCategoryService productCategoryService;

    @Operation(summary = "Create a new Product Category")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductCategoryDTO createCategory(@RequestBody ProductCategoryDTO dto) {
        return productCategoryService.createCategory(dto);
    }

    @Operation(summary = "Get all Product Categories")
    @GetMapping
    public List<ProductCategoryDTO> getAllCategories() {
        return productCategoryService.getAllCategories();
    }

    @Operation(summary = "Get Product Category by ID")
    @GetMapping("/{id}")
    public ProductCategoryDTO getCategoryById(@PathVariable("id") Long id) {
        return productCategoryService.getCategoryById(id);
    }

    @Operation(summary = "Update Product Category by ID")
    @PutMapping("/{id}")
    public ProductCategoryDTO updateCategory(@PathVariable("id") Long id,
                                             @RequestBody ProductCategoryDTO dto) {
        return productCategoryService.updateCategory(id, dto);
    }

    @Operation(summary = "Delete Product Category by ID")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable("id") Long id) {
        productCategoryService.deleteCategory(id);
    }
}