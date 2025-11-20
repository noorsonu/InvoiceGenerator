package com.main.servicesImpls;

import com.main.dtos.ProductCategoryDTO;
import com.main.entities.ProductCategoryEntity;
import com.main.mappers.InvoiceMapper;
import com.main.repositories.ProductCategoryRepository;
import com.main.services.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private InvoiceMapper invoiceMapper;

    @Override
    public ProductCategoryDTO createCategory(ProductCategoryDTO dto) {
        ProductCategoryEntity entity = invoiceMapper.productCategoryToEntity(dto);
        ProductCategoryEntity saved = productCategoryRepository.save(entity);
        return invoiceMapper.productCategoryToDto(saved);
    }

    @Override
    public ProductCategoryDTO getCategoryById(Long id) {
        return productCategoryRepository.findById(id)
                .map(invoiceMapper::productCategoryToDto)
                .orElseThrow(() -> new RuntimeException("Product category not found with ID: " + id));
    }

    @Override
    public List<ProductCategoryDTO> getAllCategories() {
        List<ProductCategoryEntity> entities = productCategoryRepository.findAll();
        return entities.stream()
                .map(invoiceMapper::productCategoryToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductCategoryDTO updateCategory(Long id, ProductCategoryDTO dto) {
        Optional<ProductCategoryEntity> existing = productCategoryRepository.findById(id);
        if (existing.isEmpty()) {
            throw new RuntimeException("Product category not found with ID: " + id);
        }
        ProductCategoryEntity entity = invoiceMapper.productCategoryToEntity(dto);
        entity.setId(id);
        entity = productCategoryRepository.save(entity);
        return invoiceMapper.productCategoryToDto(entity);
    }

    @Override
    public boolean deleteCategory(Long id) {
        Optional<ProductCategoryEntity> existing = productCategoryRepository.findById(id);
        if (existing.isPresent()) {
            productCategoryRepository.delete(existing.get());
            return true;
        }
        return false;
    }
}