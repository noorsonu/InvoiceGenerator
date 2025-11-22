package com.main.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class InvoiceDto {

	private String customerName;
    private String phoneNumber;
    private String paymentMethod;
    private String address;

    // Invoice meta
    private String invoiceNumber;
    private String invoiceDateTime; // formatted date & time when invoice was generated
    private String status;

    // Monetary fields
    private String tax;
    private String otherCharges;
    private String discount;
    private String amount;
    private String subTotal;

    // Legacy single-item fields (kept for compatibility if needed)
    private String quantity;
    private String price;
    private String product;

    // Supplier / items
    private String supplierName;
    private List<InvoiceItemDto>items;
	
}
