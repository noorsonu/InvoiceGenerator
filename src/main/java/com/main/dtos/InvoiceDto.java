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
    private String tax;
    private String otherCharges;
    private String quantity;
    private String price;
    private String product;
    private String discount;
    private String amount;
    private String subTotal;
    private String supplierName;
    private List<InvoiceItemDto>items;
	
}
