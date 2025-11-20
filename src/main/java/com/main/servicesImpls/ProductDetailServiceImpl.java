package com.main.servicesImpls;

import com.main.dtos.ProductDetailDTO;
import com.main.entities.Product;
import com.main.entities.ProductDetail;
import com.main.repositories.ProductDetailRepository;
import com.main.repositories.ProductRepository;
import com.main.services.ProductDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductDetailServiceImpl implements ProductDetailService {

    @Autowired
    private ProductDetailRepository productDetailRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public ProductDetailDTO createDetail(ProductDetailDTO dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + dto.getProductId()));

        ProductDetail detail = ProductDetail.builder()
                .product(product)
                .description(dto.getDescription())
                .build();

        ProductDetail saved = productDetailRepository.save(detail);
        return toDto(saved);
    }

    @Override
    public ProductDetailDTO getDetailById(Long id) {
        return productDetailRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Product detail not found with ID: " + id));
    }

    @Override
    public List<ProductDetailDTO> getAllDetails() {
        List<ProductDetail> details = productDetailRepository.findAll();
        return details.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public ProductDetailDTO updateDetail(Long id, ProductDetailDTO dto) {
        ProductDetail existing = productDetailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product detail not found with ID: " + id));

        if (dto.getDescription() != null) {
            existing.setDescription(dto.getDescription());
        }

        if (dto.getProductId() != null && !dto.getProductId().equals(existing.getProduct().getProductId())) {
            Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with ID: " + dto.getProductId()));
            existing.setProduct(product);
        }

        ProductDetail saved = productDetailRepository.save(existing);
        return toDto(saved);
    }

    @Override
    public boolean deleteDetail(Long id) {
        Optional<ProductDetail> existing = productDetailRepository.findById(id);
        if (existing.isPresent()) {
            productDetailRepository.delete(existing.get());
            return true;
        }
        return false;
    }

    private ProductDetailDTO toDto(ProductDetail detail) {
        return ProductDetailDTO.builder()
                .id(detail.getId())
                .productId(detail.getProduct().getProductId())
                .description(detail.getDescription())
                .build();
    }
}