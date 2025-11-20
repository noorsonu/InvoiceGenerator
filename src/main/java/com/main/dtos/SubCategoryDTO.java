package com.main.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubCategoryDTO {

    private Long id;
    private String name;

    // Parent category info
    private Long categoryId;
    private String categoryName;
}