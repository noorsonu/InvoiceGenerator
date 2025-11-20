package com.main.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDTO {

    private Long productId;
    private String productName;
    private Double price;
    private Integer stock;

    // Category info (required for product)
    private Long categoryId;
    private String categoryName;

    // SubCategory info (must belong to the selected category)
    private Long subCategoryId;
    private String subCategoryName;

    // Supplier info to show on product endpoints (optional)
    private Long supplierId;
    private String supplierName;
}
