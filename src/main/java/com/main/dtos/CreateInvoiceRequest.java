package com.main.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateInvoiceRequest {

    @NotNull(message = "Supplier id is required")
    private Long supplierId;

    // Customer details entered at invoice creation time
    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotBlank(message = "Customer phone number is required")
    private String phoneNumber;

    @NotBlank(message = "Customer address is required")
    private String address;

    @NotNull(message = "Items are required")
    private List<InvoiceItemRequest> items;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    @PositiveOrZero
    private Double tax; // overall tax amount

    @PositiveOrZero
    private Double discount;

    @PositiveOrZero
    private Double otherCharges;
}
