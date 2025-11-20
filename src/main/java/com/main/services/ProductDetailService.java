package com.main.services;

import com.main.dtos.ProductDetailDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductDetailService {

    ProductDetailDTO createDetail(ProductDetailDTO dto);

    ProductDetailDTO getDetailById(Long id);

    List<ProductDetailDTO> getAllDetails();

    ProductDetailDTO updateDetail(Long id, ProductDetailDTO dto);

    boolean deleteDetail(Long id);
}