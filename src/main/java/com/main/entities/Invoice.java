package com.main.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String invoiceNumber;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    private LocalDateTime invoiceDate;

    private String status; // e.g. PAID, UNPAID

    private Double subTotal;
    private Double tax;
    private Double discount;
    private Double otherCharges;
    private Double totalAmount;

    private String paymentMethod;

    // Customer details captured on the invoice
    private String customerName;
    private String phoneNumber;
    private String address;

    // Load items eagerly to avoid LazyInitializationException when mapping to DTOs
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<InvoiceItem> items = new ArrayList<>();
}
