package com.main.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.main.dtos.ProductDTO;

@Service
public interface ProductService {

	ProductDTO createProduct(ProductDTO dto);
    ProductDTO getProductById(Long productId);
    List<ProductDTO> getAllProducts();
	ProductDTO updateProduct(Long productId, ProductDTO dto);
	boolean deleteProduct(Long productId);
}
