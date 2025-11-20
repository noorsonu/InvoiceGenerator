package com.main.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.main.dtos.ProductDTO;
import com.main.services.ProductService;

import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
public class ProductController {

	@Autowired
    private  ProductService productService;

	@Operation(summary = "Create a new Product")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDTO createProduct(@RequestBody ProductDTO dto) {
        return productService.createProduct(dto);
    }

	@Operation(summary = "Get all Products")
    @GetMapping
    public List<ProductDTO> getAllProducts() {
        return productService.getAllProducts();
    }

	@Operation(summary = "Get Product by ID")
    @GetMapping("/{id}")
    public ProductDTO getProductById(@PathVariable("id") Long productId) {
        return productService.getProductById(productId);
    }
    
	@Operation(summary = "Update Product by ID")
    @PutMapping("/{id}")
    public ProductDTO updateProduct(@PathVariable("id") Long productId,
                                    @RequestBody ProductDTO dto) {
        return productService.updateProduct(productId, dto);
    }

	@Operation(summary = "Delete Product by ID")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable("id") Long productId) {
        productService.deleteProduct(productId);
    }
}
