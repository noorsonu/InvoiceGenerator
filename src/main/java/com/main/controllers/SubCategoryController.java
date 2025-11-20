package com.main.controllers;

import com.main.dtos.SubCategoryDTO;
import com.main.services.SubCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/subcategories")
public class SubCategoryController {

    @Autowired
    private SubCategoryService subCategoryService;

    @Operation(summary = "Create a new SubCategory")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubCategoryDTO createSubCategory(@RequestBody SubCategoryDTO dto) {
        return subCategoryService.createSubCategory(dto);
    }

    @Operation(summary = "Get all SubCategories")
    @GetMapping
    public List<SubCategoryDTO> getAllSubCategories() {
        return subCategoryService.getAllSubCategories();
    }

    @Operation(summary = "Get SubCategories by Category")
    @GetMapping("/by-category/{categoryId}")
    public List<SubCategoryDTO> getByCategory(@PathVariable Long categoryId) {
        return subCategoryService.getSubCategoriesByCategory(categoryId);
    }

    @Operation(summary = "Get SubCategory by ID")
    @GetMapping("/{id}")
    public SubCategoryDTO getSubCategoryById(@PathVariable Long id) {
        return subCategoryService.getSubCategoryById(id);
    }

    @Operation(summary = "Update SubCategory by ID")
    @PutMapping("/{id}")
    public SubCategoryDTO updateSubCategory(@PathVariable Long id,
                                            @RequestBody SubCategoryDTO dto) {
        return subCategoryService.updateSubCategory(id, dto);
    }

    @Operation(summary = "Delete SubCategory by ID")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSubCategory(@PathVariable Long id) {
        subCategoryService.deleteSubCategory(id);
    }
}