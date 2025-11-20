package com.main.controllers;

import com.main.dtos.ProductDetailDTO;
import com.main.services.ProductDetailService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/product-details")
public class ProductDetailController {

    @Autowired
    private ProductDetailService productDetailService;

    @Operation(summary = "Create Product Detail")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDetailDTO createDetail(@RequestBody ProductDetailDTO dto) {
        return productDetailService.createDetail(dto);
    }

    @Operation(summary = "Get all Product Details")
    @GetMapping
    public List<ProductDetailDTO> getAllDetails() {
        return productDetailService.getAllDetails();
    }

    @Operation(summary = "Get Product Detail by ID")
    @GetMapping("/{id}")
    public ProductDetailDTO getDetailById(@PathVariable("id") Long id) {
        return productDetailService.getDetailById(id);
    }

    @Operation(summary = "Update Product Detail by ID")
    @PutMapping("/{id}")
    public ProductDetailDTO updateDetail(@PathVariable("id") Long id,
                                         @RequestBody ProductDetailDTO dto) {
        return productDetailService.updateDetail(id, dto);
    }

    @Operation(summary = "Delete Product Detail by ID")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDetail(@PathVariable("id") Long id) {
        productDetailService.deleteDetail(id);
    }
}