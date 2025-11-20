package com.main.servicesImpls;

import com.main.dtos.SubCategoryDTO;
import com.main.entities.ProductCategoryEntity;
import com.main.entities.SubCategoryEntity;
import com.main.mappers.InvoiceMapper;
import com.main.repositories.ProductCategoryRepository;
import com.main.repositories.SubCategoryRepository;
import com.main.services.SubCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubCategoryServiceImpl implements SubCategoryService {

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private InvoiceMapper invoiceMapper;

    @Override
    public SubCategoryDTO createSubCategory(SubCategoryDTO dto) {
        ProductCategoryEntity category = productCategoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Product category not found with ID: " + dto.getCategoryId()));

        SubCategoryEntity entity = invoiceMapper.subCategoryToEntity(dto);
        entity.setCategory(category);
        entity.setSubcategoryId(null);

        SubCategoryEntity saved = subCategoryRepository.save(entity);
        return invoiceMapper.subCategoryToDto(saved);
    }

    @Override
    public SubCategoryDTO getSubCategoryById(Long id) {
        return subCategoryRepository.findById(id)
                .map(invoiceMapper::subCategoryToDto)
                .orElseThrow(() -> new RuntimeException("SubCategory not found with ID: " + id));
    }

    @Override
    public List<SubCategoryDTO> getAllSubCategories() {
        return subCategoryRepository.findAll().stream()
                .map(invoiceMapper::subCategoryToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubCategoryDTO> getSubCategoriesByCategory(Long categoryId) {
        return subCategoryRepository.findByCategoryId(categoryId).stream()
                .map(invoiceMapper::subCategoryToDto)
                .collect(Collectors.toList());
    }

    @Override
    public SubCategoryDTO updateSubCategory(Long id, SubCategoryDTO dto) {
        Optional<SubCategoryEntity> existingOpt = subCategoryRepository.findById(id);
        if (existingOpt.isEmpty()) {
            throw new RuntimeException("SubCategory not found with ID: " + id);
        }

        SubCategoryEntity existing = existingOpt.get();

        if (dto.getName() != null) {
            existing.setSubcategoryName(dto.getName());
        }

        if (dto.getCategoryId() != null) {
            ProductCategoryEntity category = productCategoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Product category not found with ID: " + dto.getCategoryId()));
            existing.setCategory(category);
        }

        SubCategoryEntity saved = subCategoryRepository.save(existing);
        return invoiceMapper.subCategoryToDto(saved);
    }

    @Override
    public boolean deleteSubCategory(Long id) {
        Optional<SubCategoryEntity> existing = subCategoryRepository.findById(id);
        if (existing.isPresent()) {
            subCategoryRepository.delete(existing.get());
            return true;
        }
        return false;
    }
}