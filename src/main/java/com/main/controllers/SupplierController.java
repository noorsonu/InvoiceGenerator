package com.main.controllers;

import com.main.dtos.SupplierDto;
import com.main.entities.Supplier;
import com.main.repositories.SupplierRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/suppliers")
public class SupplierController {

	private final SupplierRepository supplierRepository;
	
	public SupplierController(SupplierRepository supplierRepository) {
		this.supplierRepository = supplierRepository;
	}

	@Operation(summary = "Create a new supplier")
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public SupplierDto createSupplier(@RequestBody @Valid SupplierDto dto) {
		Supplier entity = toEntity(dto);
		entity.setId(null);
		Supplier saved = supplierRepository.save(entity);
		return toDto(saved);
	}

	@Operation(summary = "Get all suppliers")
	@GetMapping
	public List<SupplierDto> getSuppliers() {
		return supplierRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
	}

	@Operation(summary = "Get supplier by ID")
	@GetMapping("/{id}")
	public SupplierDto getSupplier(@PathVariable Long id) {
		Supplier supplier = supplierRepository.findById(id).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Supplier not found with id: " + id));
		return toDto(supplier);
	}

	@Operation(summary = "Update supplier by ID")
	@PutMapping("/{id}")
	public SupplierDto updateSupplier(@PathVariable Long id, @RequestBody @Valid SupplierDto dto) {
		Supplier existing = supplierRepository.findById(id).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Supplier not found with id: " + id));
		existing.setName(dto.getName());
		existing.setEmail(dto.getEmail());
		existing.setPhoneNumber(dto.getPhoneNumber());
		existing.setAddress(dto.getAddress());
		Supplier saved = supplierRepository.save(existing);
		return toDto(saved);
	}

	@Operation(summary = "Delete supplier by ID")
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteSupplier(@PathVariable Long id) {
		supplierRepository.deleteById(id);
	}

	private SupplierDto toDto(Supplier s) {
		return new SupplierDto(s.getId(), s.getName(), s.getEmail(), s.getPhoneNumber(), s.getAddress());
	}

	private Supplier toEntity(SupplierDto dto) {
		Supplier s = new Supplier();
		s.setId(dto.getId());
		s.setName(dto.getName());
		s.setEmail(dto.getEmail());
		s.setPhoneNumber(dto.getPhoneNumber());
		s.setAddress(dto.getAddress());
		return s;
	}
}
