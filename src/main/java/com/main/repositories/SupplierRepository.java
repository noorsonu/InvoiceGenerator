package com.main.repositories;

import com.main.entities.ProductCategoryEntity;
import com.main.entities.Supplier;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

	Optional<ProductCategoryEntity> findByName(String supplierName);
}


