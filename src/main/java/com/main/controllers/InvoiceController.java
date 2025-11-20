package com.main.controllers;

import com.main.dtos.CreateInvoiceRequest;
import com.main.dtos.ErrorResponse;
import com.main.dtos.InvoiceDto;
import com.main.dtos.InvoiceItemDto;
import com.main.dtos.InvoiceItemRequest;
import com.main.entities.Invoice;
import com.main.entities.InvoiceItem;
import com.main.entities.Product;
import com.main.entities.Supplier;
import com.main.repositories.InvoiceRepository;
import com.main.repositories.ProductRepository;
import com.main.repositories.SupplierRepository;
import com.main.services.PdfGeneratorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/invoices")
public class InvoiceController {

	private final SupplierRepository supplierRepository;
	private final ProductRepository productRepository;
	private final InvoiceRepository invoiceRepository;
	private final PdfGeneratorService pdfGeneratorService;

	public InvoiceController(SupplierRepository supplierRepository, ProductRepository productRepository,
			InvoiceRepository invoiceRepository, PdfGeneratorService pdfGeneratorService) {
		this.supplierRepository = supplierRepository;
		this.productRepository = productRepository;
		this.invoiceRepository = invoiceRepository;
		this.pdfGeneratorService = pdfGeneratorService;
	}

	@Operation(summary = "Create multi-product invoice and return PDF")
	@PostMapping
	public ResponseEntity<byte[]> createInvoice(@RequestBody @Valid CreateInvoiceRequest request) {
		Supplier supplier = supplierRepository.findById(request.getSupplierId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Supplier not found"));

		List<InvoiceItem> invoiceItems = new ArrayList<>();
		double subTotal = 0.0;

		for (InvoiceItemRequest itemReq : request.getItems()) {
			Product product = productRepository.findById(itemReq.getProductId())
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
							"Product not found with id: " + itemReq.getProductId()));

			int currentStock = product.getStock() != null ? product.getStock() : 0;
			int quantity = itemReq.getQuantity();
			if (currentStock < quantity) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough stock for product "
						+ product.getProductName() + ". Available: " + currentStock + ", requested: " + quantity);
			}

			double unitPrice = product.getPrice() != null ? product.getPrice() : 0.0;
			double lineTotal = unitPrice * quantity;
			subTotal += lineTotal;

			InvoiceItem invoiceItem = new InvoiceItem();
			invoiceItem.setProduct(product);
			invoiceItem.setQuantity(quantity);
			invoiceItem.setUnitPrice(unitPrice);
			invoiceItem.setLineTotal(lineTotal);
			invoiceItems.add(invoiceItem);
		}

		for (InvoiceItem item : invoiceItems) {
			Product p = item.getProduct();
			int currentStock = p.getStock() != null ? p.getStock() : 0;
			p.setStock(currentStock - item.getQuantity());
			productRepository.save(p);
		}

		double tax = request.getTax() != null ? request.getTax() : 0.0;
		double discount = request.getDiscount() != null ? request.getDiscount() : 0.0;
		double otherCharges = request.getOtherCharges() != null ? request.getOtherCharges() : 0.0;
		double totalAmount = subTotal + tax + otherCharges - discount;

		Invoice invoice = new Invoice();
		invoice.setSupplier(supplier);
		invoice.setInvoiceDate(LocalDateTime.now());
		invoice.setStatus("PAID");
		invoice.setPaymentMethod(request.getPaymentMethod());
		invoice.setSubTotal(subTotal);
		invoice.setTax(tax);
		invoice.setDiscount(discount);
		invoice.setOtherCharges(otherCharges);
		invoice.setTotalAmount(totalAmount);

		String invoiceNumber = "INV-" + System.currentTimeMillis();
		invoice.setInvoiceNumber(invoiceNumber);

		for (InvoiceItem item : invoiceItems) {
			item.setInvoice(invoice);
		}
		invoice.setItems(invoiceItems);

		Invoice saved = invoiceRepository.save(invoice);

		InvoiceDto dto = toDto(saved);
		byte[] pdfBytes = pdfGeneratorService.generateInvoicePdf(dto);

		return ResponseEntity.ok().header("Content-Type", "application/pdf")
				.header("Content-Disposition", "attachment; filename=" + saved.getInvoiceNumber() + ".pdf")
				.body(pdfBytes);
	}

	@Operation(summary = "Get all invoice")
	@GetMapping
	public List<InvoiceDto> getAllInvoices() {
	    List<Invoice> invoices = invoiceRepository.findAll();
	    return invoices.stream()
	            .map(this::toDto)
	            .collect(Collectors.toList());
	}

	@Operation(summary = "Download invoice PDF by ID")
	@GetMapping("/{id}/pdf")
	public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long id) {
		Invoice invoice = invoiceRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));
		InvoiceDto dto = toDto(invoice);
		byte[] pdfBytes = pdfGeneratorService.generateInvoicePdf(dto);

		return ResponseEntity.ok().header("Content-Type", "application/pdf")
				.header("Content-Disposition", "attachment; filename=" + invoice.getInvoiceNumber() + ".pdf")
				.body(pdfBytes);
	}

    private InvoiceDto toDto(Invoice invoice) {
        InvoiceDto dto = new InvoiceDto();
        if (invoice.getSupplier() != null) {
            dto.setSupplierName(invoice.getSupplier().getName());
            dto.setCustomerName(invoice.getSupplier().getName());
            dto.setPhoneNumber(invoice.getSupplier().getPhoneNumber());
            dto.setAddress(invoice.getSupplier().getAddress());
        }
        dto.setPaymentMethod(invoice.getPaymentMethod());
		dto.setTax(String.format("%.2f", invoice.getTax() != null ? invoice.getTax() : 0.0));
		dto.setDiscount(String.format("%.2f", invoice.getDiscount() != null ? invoice.getDiscount() : 0.0));
		dto.setOtherCharges(String.format("%.2f", invoice.getOtherCharges() != null ? invoice.getOtherCharges() : 0.0));
		dto.setSubTotal(String.format("%.2f", invoice.getSubTotal() != null ? invoice.getSubTotal() : 0.0));
		dto.setAmount(String.format("%.2f", invoice.getTotalAmount() != null ? invoice.getTotalAmount() : 0.0));

		List<InvoiceItemDto> itemDtos = invoice.getItems().stream()
				.map(item -> new InvoiceItemDto(item.getProduct() != null ? item.getProduct().getProductName() : "",
						String.format("%.2f", item.getUnitPrice() != null ? item.getUnitPrice() : 0.0),
						String.valueOf(item.getQuantity() != null ? item.getQuantity() : 0),
						String.format("%.2f", item.getLineTotal() != null ? item.getLineTotal() : 0.0)))
				.collect(Collectors.toList());

		dto.setItems(itemDtos);
		return dto;
	}
}
