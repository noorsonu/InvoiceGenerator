package com.main.services;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.main.dtos.InvoiceDto;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PdfGeneratorService {

    public byte[] generateInvoicePdf(InvoiceDto invoice) {
        try {
            // Base document setup
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            PdfWriter.getInstance(document, out);
            document.open();

            // ==== HEADER (Brand + meta info) ====
            Font brandFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Font smallBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.DARK_GRAY);
            Font small = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.DARK_GRAY);

            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{2, 1.3f});

            
            Paragraph brand = new Paragraph("VCS", brandFont);
            brand.setSpacingAfter(4f);

            Paragraph address = new Paragraph("Lucknow, Uttar Pradesh\n 274001", small);

            PdfPCell leftHeader = new PdfPCell();
            leftHeader.setBorder(PdfPCell.NO_BORDER);
            leftHeader.addElement(brand);
            leftHeader.addElement(address);
            headerTable.addCell(leftHeader);

            // Right: Invoice meta (static/sample data)
            PdfPCell rightHeader = new PdfPCell();
            rightHeader.setBorder(PdfPCell.NO_BORDER);

            Paragraph invoiceNo = new Paragraph("INVOICE NO: #VL25000351", smallBold);
            Paragraph date = new Paragraph("DATE: 22 Mar, 2021 9:58 PM", small);
            Paragraph paymentStatus = new Paragraph("PAYMENT STATUS: Paid", small);
            Paragraph totalAmount = new Paragraph("TOTAL AMOUNT: $" + (invoice.getAmount() != null ? invoice.getAmount() : "0.00"), smallBold);

            rightHeader.addElement(invoiceNo);
            rightHeader.addElement(date);
            rightHeader.addElement(paymentStatus);
            rightHeader.addElement(totalAmount);
            headerTable.addCell(rightHeader);

            document.add(headerTable);

            Paragraph spacer = new Paragraph(" ");
            spacer.setSpacingBefore(10f);
            document.add(spacer);

            // ==== BILLING / SHIPPING ADDRESSES ====
            PdfPTable addressTable = new PdfPTable(2);
            addressTable.setWidthPercentage(100);
            addressTable.setSpacingBefore(10f);
            addressTable.setWidths(new float[]{1, 1});

            // Billing address
            PdfPCell billingCell = new PdfPCell();
            billingCell.setBorder(PdfPCell.NO_BORDER);

            Paragraph billingTitle = new Paragraph("BILLING ADDRESS", smallBold);
            Paragraph billingName = new Paragraph(invoice.getCustomerName() != null ? invoice.getCustomerName() : "Customer Name", small);
            Paragraph billingAddress = new Paragraph(invoice.getAddress() != null ? invoice.getAddress() : "Customer Address", small);
            Paragraph billingPhone = new Paragraph("Phone: " + (invoice.getPhoneNumber() != null ? invoice.getPhoneNumber() : ""), small);

            billingCell.addElement(billingTitle);
            billingCell.addElement(billingName);
            billingCell.addElement(billingAddress);
            billingCell.addElement(billingPhone);
            addressTable.addCell(billingCell);

            // Shipping address (reuse same data for now)
            PdfPCell shippingCell = new PdfPCell();
            shippingCell.setBorder(PdfPCell.NO_BORDER);

            Paragraph shippingTitle = new Paragraph("SHIPPING ADDRESS", smallBold);
            Paragraph shippingName = new Paragraph(invoice.getCustomerName() != null ? invoice.getCustomerName() : "Customer Name", small);
            Paragraph shippingAddress = new Paragraph(invoice.getAddress() != null ? invoice.getAddress() : "Customer Address", small);
            Paragraph shippingPhone = new Paragraph("Phone: " + (invoice.getPhoneNumber() != null ? invoice.getPhoneNumber() : ""), small);

            shippingCell.addElement(shippingTitle);
            shippingCell.addElement(shippingName);
            shippingCell.addElement(shippingAddress);
            shippingCell.addElement(shippingPhone);
            addressTable.addCell(shippingCell);

            document.add(addressTable);

            // ==== PRODUCT DETAILS TABLE ====
            PdfPTable itemTable = new PdfPTable(5);
            itemTable.setWidthPercentage(100);
            itemTable.setSpacingBefore(20f);
            itemTable.setWidths(new float[]{0.6f, 3.2f, 1.2f, 1.0f, 1.4f});

            Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, BaseColor.WHITE);
            BaseColor headerBg = new BaseColor(248, 249, 252);
            BaseColor headerText = new BaseColor(33, 37, 41);

            // Header cells
            addHeaderCell(itemTable, "#", tableHeaderFont, headerBg, headerText);
            addHeaderCell(itemTable, "Product Details", tableHeaderFont, headerBg, headerText);
            addHeaderCell(itemTable, "Rate", tableHeaderFont, headerBg, headerText);
            addHeaderCell(itemTable, "Quantity", tableHeaderFont, headerBg, headerText);
            addHeaderCell(itemTable, "Amount", tableHeaderFont, headerBg, headerText);

            Font itemFont = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.BLACK);

            // Single item row based on current DTO (you can later extend this to a list)
            itemTable.addCell(createBodyCell("01", itemFont, Element.ALIGN_LEFT));
            itemTable.addCell(createBodyCell(invoice.getProduct() != null ? invoice.getProduct() : "Product", itemFont, Element.ALIGN_LEFT));
            itemTable.addCell(createBodyCell(invoice.getPrice() != null ? invoice.getPrice() : "0.00", itemFont, Element.ALIGN_RIGHT));
            itemTable.addCell(createBodyCell(invoice.getQuantity() != null ? invoice.getQuantity() : "1", itemFont, Element.ALIGN_RIGHT));
            itemTable.addCell(createBodyCell(invoice.getAmount() != null ? invoice.getAmount() : "0.00", itemFont, Element.ALIGN_RIGHT));

            document.add(itemTable);

            // ==== TOTALS (right aligned) ====
            PdfPTable totalsTable = new PdfPTable(2);
            totalsTable.setWidthPercentage(40);
            totalsTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalsTable.setSpacingBefore(10f);
            totalsTable.setWidths(new float[]{1.5f, 1f});

            addTotalsRow(totalsTable, "Sub Total", invoice.getSubTotal());
            addTotalsRow(totalsTable, "Estimated Tax", invoice.getTax());
            addTotalsRow(totalsTable, "Discount", invoice.getDiscount());
            addTotalsRow(totalsTable, "Other Charges", invoice.getOtherCharges());
            addTotalsRow(totalsTable, "Total Amount", invoice.getAmount());

            document.add(totalsTable);

            // ==== PAYMENT DETAILS ====
            Paragraph paymentTitle = new Paragraph("PAYMENT DETAILS:", smallBold);
            paymentTitle.setSpacingBefore(20f);
            document.add(paymentTitle);

            Paragraph paymentMethod = new Paragraph("Payment Method: " + (invoice.getPaymentMethod() != null ? invoice.getPaymentMethod() : ""), small);
            Paragraph cardHolder = new Paragraph("Card Holder: " + (invoice.getCustomerName() != null ? invoice.getCustomerName() : ""), small);
            Paragraph cardNumber = new Paragraph("Card Number: xxxx xxxx xxxx 1234", small);
            Paragraph total = new Paragraph("Total Amount: $" + (invoice.getAmount() != null ? invoice.getAmount() : "0.00"), small);

            document.add(paymentMethod);
            document.add(cardHolder);
            document.add(cardNumber);
            document.add(total);

            // ==== NOTES BOX ====
            PdfPTable notesTable = new PdfPTable(1);
            notesTable.setWidthPercentage(100);
            notesTable.setSpacingBefore(20f);

            PdfPCell notesCell = new PdfPCell();
            notesCell.setBackgroundColor(new BaseColor(232, 244, 253));
            notesCell.setPadding(10f);

            Paragraph notesTitle = new Paragraph("NOTES", smallBold);
            Paragraph notesText = new Paragraph(
                    "All accounts are to be paid within 7 days from receipt of invoice. " +
                            "If account is not paid within 7 days the credits details supplied as confirmation " +
                            "of work undertaken will be charged the agreed quoted fee noted above.",
                    small);

            notesCell.addElement(notesTitle);
            notesCell.addElement(notesText);
            notesTable.addCell(notesCell);

            document.add(notesTable);

            document.close();
            return out.toByteArray();
        } catch (DocumentException e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }

    private void addHeaderCell(PdfPTable table, String text, Font font, BaseColor bg, BaseColor textColor) {
        Font headerFontColored = new Font(font.getFamily(), font.getSize(), Font.BOLD, textColor);
        PdfPCell cell = new PdfPCell(new Phrase(text, headerFontColored));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(bg);
        cell.setPadding(8f);
        table.addCell(cell);
    }

    private PdfPCell createBodyCell(String text, Font font, int horizontalAlignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", font));
        cell.setHorizontalAlignment(horizontalAlignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(6f);
        return cell;
    }

    private void addTotalsRow(PdfPTable table, String label, String value) {
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.DARK_GRAY);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, BaseColor.BLACK);

        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(PdfPCell.NO_BORDER);
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        labelCell.setPadding(4f);

        PdfPCell valueCell = new PdfPCell(new Phrase(value != null ? value : "0.00", valueFont));
        valueCell.setBorder(PdfPCell.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valueCell.setPadding(4f);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }
}

