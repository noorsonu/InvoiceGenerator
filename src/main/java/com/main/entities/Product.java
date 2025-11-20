package com.main.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    // Minimal fields needed for stock management and invoices
    private String productName;
    private Double price;
    private Integer stock;

    // Supplier owning this product
    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    // Direct category for this product
    @ManyToOne
    @JoinColumn(name = "category_id")
    private ProductCategoryEntity category;

    // Link to subcategory (for grouping products), matches mappedBy in SubCategoryEntity
    @ManyToOne
    @JoinColumn(name = "subcategory_id")
    private SubCategoryEntity subCategory;

    // Relationship to invoice items so we can see where a product is used
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceItem> invoiceItems = new ArrayList<>();
}
