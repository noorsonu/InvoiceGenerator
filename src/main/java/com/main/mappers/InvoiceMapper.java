package com.main.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.main.dtos.AdminDto;
import com.main.dtos.ProductDTO;
import com.main.dtos.ProductCategoryDTO;
import com.main.dtos.SubCategoryDTO;
import com.main.entities.Admin;
import com.main.entities.Product;
import com.main.entities.ProductCategoryEntity;
import com.main.entities.SubCategoryEntity;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface InvoiceMapper {

    // Admin
    Admin dtoToAdmin(AdminDto adminDto);
    AdminDto adminToDto(Admin admin);
    List<AdminDto> adminsToAdminDtos(List<Admin> admins);
    List<Admin> adminDtosToAdmins(List<AdminDto> adminDtos);

    // Product
    @Mapping(source = "supplier.id",target = "supplierId")
    @Mapping(source = "supplier.name", target = "supplierName")
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "subCategory.subcategoryId", target = "subCategoryId")
    @Mapping(source = "subCategory.subcategoryName", target = "subCategoryName")
    ProductDTO toDto(Product product);

    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "subCategory", ignore = true)
    Product toEntity(ProductDTO productDTO);

    // Product Category
    ProductCategoryDTO productCategoryToDto(ProductCategoryEntity entity);
    ProductCategoryEntity productCategoryToEntity(ProductCategoryDTO dto);

    // SubCategory
    @Mapping(source = "subcategoryId", target = "id")
    @Mapping(source = "subcategoryName", target = "name")
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    SubCategoryDTO subCategoryToDto(SubCategoryEntity entity);

    @Mapping(source = "id", target = "subcategoryId")
    @Mapping(source = "name", target = "subcategoryName")
    @Mapping(target = "category", ignore = true)
    SubCategoryEntity subCategoryToEntity(SubCategoryDTO dto);
}
