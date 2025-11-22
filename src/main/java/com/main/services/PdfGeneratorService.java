package com.main.services;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
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

			PdfWriter writer = PdfWriter.getInstance(document, out);
			document.open();

			// ==== BACKGROUND LOGO (watermark) ====
			try {
				// Make sure the logo image `logo.png` is placed in src/main/resources
				Image bg = Image.getInstance(getClass().getResource("/logo.png"));
				bg.setRotationDegrees(0);
				bg.scaleToFit(PageSize.A4.getWidth() * 0.7f, PageSize.A4.getHeight() * 0.7f);
				// Center on page
				float x = (PageSize.A4.getWidth() - bg.getScaledWidth()) / 2;
				float y = (PageSize.A4.getHeight() - bg.getScaledHeight()) / 2;
				bg.setAbsolutePosition(x, y);

				PdfContentByte canvas = writer.getDirectContentUnder();
				PdfGState gState = new PdfGState();
				// Very light opacity so content is clearly readable
				gState.setFillOpacity(0.1f);
				canvas.saveState();
				canvas.setGState(gState);
				canvas.addImage(bg);
				canvas.restoreState();
			} catch (Exception ignored) {
				// If logo is missing or cannot be loaded, continue generating the PDF without
				// background.
			}

			// ==== HEADER (Brand + meta info) ====
			Font brandFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
			Font smallBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.DARK_GRAY);
			Font small = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.DARK_GRAY);

			PdfPTable headerTable = new PdfPTable(2);
			headerTable.setWidthPercentage(100);
			headerTable.setWidths(new float[] { 2, 1.3f });

			// Left: Brand / company info
			Paragraph brand = new Paragraph("VCS", brandFont);
			brand.setSpacingAfter(4f);

			Paragraph address = new Paragraph("Lucknow, Uttar Pradesh\n 274001", small);

			PdfPCell leftHeader = new PdfPCell();
			leftHeader.setBorder(PdfPCell.NO_BORDER);
			leftHeader.addElement(brand);
			leftHeader.addElement(address);
			headerTable.addCell(leftHeader);

			// Right: Invoice meta (dynamic)
			PdfPCell rightHeader = new PdfPCell();
			rightHeader.setBorder(PdfPCell.NO_BORDER);

			Paragraph invoiceNo = new Paragraph(
					"INVOICE NO: " + (invoice.getInvoiceNumber() != null ? invoice.getInvoiceNumber() : ""), smallBold);
			Paragraph date = new Paragraph(
					"DATE: " + (invoice.getInvoiceDateTime() != null ? invoice.getInvoiceDateTime() : ""), small);

			String statusText = invoice.getStatus() != null ? invoice.getStatus() : "Paid";
			String method = invoice.getPaymentMethod() != null ? invoice.getPaymentMethod() : "";
			boolean isOnline = !method.equalsIgnoreCase("CASH") && !method.isBlank();
			String paymentStatusText = isOnline ? statusText + " (Online - " + method + ")"
					: statusText + (!method.isBlank() ? " (" + method + ")" : "");

			Paragraph paymentStatus = new Paragraph("PAYMENT STATUS: " + paymentStatusText, small);
			Paragraph totalAmount = new Paragraph(
					"TOTAL AMOUNT: ₹ " + (invoice.getAmount() != null ? invoice.getAmount() : "0.00"), smallBold);

			rightHeader.addElement(invoiceNo);
			rightHeader.addElement(date);
			rightHeader.addElement(paymentStatus);
			rightHeader.addElement(totalAmount);
			headerTable.addCell(rightHeader);

			document.add(headerTable);

			Paragraph spacer = new Paragraph(" ");
			spacer.setSpacingBefore(10f);
			document.add(spacer);

			// ==== CUSTOMER DETAILS (replaces Billing / Shipping) ====
			PdfPTable customerTable = new PdfPTable(1);
			customerTable.setWidthPercentage(100);
			customerTable.setSpacingBefore(10f);

			PdfPCell customerCell = new PdfPCell();
			customerCell.setBorder(PdfPCell.NO_BORDER);

			Paragraph customerTitle = new Paragraph("CUSTOMER DETAILS", smallBold);
			Paragraph customerName = new Paragraph(
					"Name: " + (invoice.getCustomerName() != null ? invoice.getCustomerName() : "Customer Name"),
					small);
			Paragraph customerPhone = new Paragraph(
					"Phone: " + (invoice.getPhoneNumber() != null ? invoice.getPhoneNumber() : ""), small);
			Paragraph customerAddress = new Paragraph(
					"Address: " + (invoice.getAddress() != null ? invoice.getAddress() : "Customer Address"), small);
			Paragraph customerDateTime = new Paragraph("Invoice Date & Time: "
					+ (invoice.getInvoiceDateTime() != null ? invoice.getInvoiceDateTime() : ""), small);

			customerCell.addElement(customerTitle);
			customerCell.addElement(customerName);
			customerCell.addElement(customerPhone);
			customerCell.addElement(customerAddress);
			customerCell.addElement(customerDateTime);
			customerTable.addCell(customerCell);

			document.add(customerTable);

			// ==== PRODUCT DETAILS TABLE ====
			PdfPTable itemTable = new PdfPTable(5);
			itemTable.setWidthPercentage(100);
			itemTable.setSpacingBefore(20f);
			itemTable.setWidths(new float[] { 0.6f, 3.2f, 1.2f, 1.0f, 1.4f });

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

			// Multi-product rows based on InvoiceItemDto list
			if (invoice.getItems() != null && !invoice.getItems().isEmpty()) {
				int index = 1;
				for (var item : invoice.getItems()) {
					String rowNo = String.format("%02d", index++);
					itemTable.addCell(createBodyCell(rowNo, itemFont, Element.ALIGN_LEFT));
					itemTable.addCell(createBodyCell(item.getProductName() != null ? item.getProductName() : "",
							itemFont, Element.ALIGN_LEFT));
					itemTable.addCell(createBodyCell(item.getUnitPrice() != null ? item.getUnitPrice() : "0.00",
							itemFont, Element.ALIGN_RIGHT));
					itemTable.addCell(createBodyCell(item.getQuantity() != null ? item.getQuantity() : "0", itemFont,
							Element.ALIGN_RIGHT));
					itemTable.addCell(createBodyCell(item.getLineTotal() != null ? item.getLineTotal() : "0.00",
							itemFont, Element.ALIGN_RIGHT));
				}
			} else {
				// Fallback single empty row if no items are present
				itemTable.addCell(createBodyCell("01", itemFont, Element.ALIGN_LEFT));
				itemTable.addCell(createBodyCell("Product", itemFont, Element.ALIGN_LEFT));
				itemTable.addCell(createBodyCell("0.00", itemFont, Element.ALIGN_RIGHT));
				itemTable.addCell(createBodyCell("0", itemFont, Element.ALIGN_RIGHT));
				itemTable.addCell(createBodyCell("0.00", itemFont, Element.ALIGN_RIGHT));
			}

			document.add(itemTable);

			// ==== TOTALS (right aligned) ====
			PdfPTable totalsTable = new PdfPTable(2);
			totalsTable.setWidthPercentage(40);
			totalsTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
			totalsTable.setSpacingBefore(10f);
			totalsTable.setWidths(new float[] { 1.5f, 1f });

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

			String methodForDetails = invoice.getPaymentMethod() != null ? invoice.getPaymentMethod() : "";
			Paragraph paymentMethod = new Paragraph(
					"Payment Method: " + (!methodForDetails.isBlank() ? methodForDetails : "N/A"), small);
			document.add(paymentMethod);

			// Show more specific details for common online methods
			if (!methodForDetails.isBlank()) {
				if (methodForDetails.equalsIgnoreCase("CARD")) {
					Paragraph cardHolder = new Paragraph(
							"Card Holder: " + (invoice.getCustomerName() != null ? invoice.getCustomerName() : ""),
							small);
					Paragraph cardNumber = new Paragraph("Card Number: xxxx xxxx xxxx 1234", small);
					document.add(cardHolder);
					document.add(cardNumber);
				} else if (methodForDetails.equalsIgnoreCase("PAYTM")
						|| methodForDetails.toLowerCase().contains("paytm")) {
					Paragraph wallet = new Paragraph("Wallet: Paytm", small);
					document.add(wallet);
				} else if (methodForDetails.toLowerCase().contains("upi")) {
					Paragraph upi = new Paragraph("Payment Type: UPI", small);
					document.add(upi);
				}
			}

			Paragraph total = new Paragraph(
					"Total Amount: ₹ " + (invoice.getAmount() != null ? invoice.getAmount() : "0.00"), small);
			document.add(total);

			// ==== NOTES BOX ====
			PdfPTable notesTable = new PdfPTable(1);
			notesTable.setWidthPercentage(100);
			notesTable.setSpacingBefore(20f);

			PdfPCell notesCell = new PdfPCell();
			notesCell.setBackgroundColor(new BaseColor(232, 244, 253));
			notesCell.setPadding(10f);

			Paragraph notesTitle = new Paragraph("NOTES", smallBold);
			Paragraph notesText = new Paragraph("All accounts are to be paid within 7 days from receipt of invoice. "
					+ "If account is not paid within 7 days the credits details supplied as confirmation "
					+ "of work undertaken will be charged the agreed quoted fee noted above.", small);

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
