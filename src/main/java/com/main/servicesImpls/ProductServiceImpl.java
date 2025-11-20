package com.main.servicesImpls;

import com.main.dtos.ProductDTO;
import com.main.entities.Product;
import com.main.entities.ProductCategoryEntity;
import com.main.entities.SubCategoryEntity;
import com.main.entities.Supplier;
import com.main.mappers.InvoiceMapper;
import com.main.repositories.ProductCategoryRepository;
import com.main.repositories.ProductRepository;
import com.main.repositories.SubCategoryRepository;
import com.main.repositories.SupplierRepository;
import com.main.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public  class ProductServiceImpl implements ProductService {

	@Autowired
    private  ProductRepository productRepository;
	@Autowired
    private  InvoiceMapper productMapper;
	@Autowired
    private SupplierRepository supplierRepository;
	@Autowired
    private ProductCategoryRepository productCategoryRepository;
	@Autowired
    private SubCategoryRepository subCategoryRepository;

    @Override
    public ProductDTO createProduct(ProductDTO dto) {
        Product product = productMapper.toEntity(dto);

        // Supplier is optional
        if (dto.getSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                    .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + dto.getSupplierId()));
            product.setSupplier(supplier);
        }

        // Category and SubCategory are required and must match
        if (dto.getCategoryId() == null) {
            throw new RuntimeException("Category is required for a Product.");
        }
        if (dto.getSubCategoryId() == null) {
            throw new RuntimeException("SubCategory is required for a Product.");
        }

        ProductCategoryEntity category = productCategoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Product category not found with ID: " + dto.getCategoryId()));

        SubCategoryEntity subCategory = subCategoryRepository.findById(dto.getSubCategoryId())
                .orElseThrow(() -> new RuntimeException("SubCategory not found with ID: " + dto.getSubCategoryId()));

        if (subCategory.getCategory() == null || !subCategory.getCategory().getId().equals(category.getId())) {
            throw new RuntimeException("Category and SubCategory do not match.");
        }

        product.setCategory(category);
        product.setSubCategory(subCategory);

        Product saved = productRepository.save(product);
        return productMapper.toDto(saved);
    }

    @Override
    public ProductDTO getProductById(Long productId) {
        return productRepository.findById(productId)
                .map(productMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(productMapper::toDto).collect(Collectors.toList());
    }
    
    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO dto) {
        Optional<Product> existing = productRepository.findById(productId);

        if (existing.isPresent()) {
            Product entity = existing.get();
            // Update basic fields from DTO
            entity.setProductName(dto.getProductName());
            entity.setPrice(dto.getPrice());
            entity.setStock(dto.getStock());

            // Update supplier if supplierId provided (still optional)
            if (dto.getSupplierId() != null) {
                Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                        .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + dto.getSupplierId()));
                entity.setSupplier(supplier);
            }

            // Determine final category to validate subcategory against
            ProductCategoryEntity categoryForValidation = null;
            if (dto.getCategoryId() != null) {
                categoryForValidation = productCategoryRepository.findById(dto.getCategoryId())
                        .orElseThrow(() -> new RuntimeException("Product category not found with ID: " + dto.getCategoryId()));
                entity.setCategory(categoryForValidation);
            } else if (entity.getCategory() != null) {
                categoryForValidation = entity.getCategory();
            }

            // If subCategoryId is provided, enforce the Category + SubCategory match
            if (dto.getSubCategoryId() != null) {
                if (categoryForValidation == null) {
                    throw new RuntimeException("Category is required when setting a SubCategory.");
                }

                SubCategoryEntity subCategory = subCategoryRepository.findById(dto.getSubCategoryId())
                        .orElseThrow(() -> new RuntimeException("SubCategory not found with ID: " + dto.getSubCategoryId()));

                if (subCategory.getCategory() == null ||
                        !subCategory.getCategory().getId().equals(categoryForValidation.getId())) {
                    throw new RuntimeException("Category and SubCategory do not match.");
                }

                entity.setSubCategory(subCategory);
            }

            entity = productRepository.save(entity);
            return productMapper.toDto(entity);
        } else {
            throw new RuntimeException("Product not found with ID: " + productId);
        }
    }

    @Override
    public boolean deleteProduct(Long productId) {
        Optional<Product> existing = productRepository.findById(productId);
        if (existing.isPresent()) {
            productRepository.delete(existing.get());
            return true;
        } else {
            return false;
        }
    }
}
