package org.freightfox.dynamicpdfgenerator.service;

import lombok.extern.slf4j.Slf4j;
import org.freightfox.dynamicpdfgenerator.dto.InvoiceData;
import org.freightfox.dynamicpdfgenerator.exception.PdfGenerationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class PdfService {
    private final String STORAGE_DIR = "src/main/resources/storage";

    @Autowired
    private SpringTemplateEngine templateEngine;

    // Generate a PDF file from InvoiceData
    public File generatePdf(InvoiceData invoiceData) {
        try {
            String hash = String.valueOf(invoiceData.hashCode());
            Path path = Paths.get(STORAGE_DIR, hash + ".pdf");

            if (Files.exists(path)) {
                log.info("PDF file already exists at: " + path);
                return path.toFile();
            }

            String htmlContent = generateHtml(invoiceData);
            byte[] pdfBytes = generatePdfFromHtml(htmlContent);

            try (FileOutputStream fos = new FileOutputStream(path.toFile())) {
                fos.write(pdfBytes);
            }
            log.info("PDF file generated at: " + path);
            return path.toFile();
        } catch (Exception e) {
            log.error("Error generating PDF: " + e.getMessage());
            throw new PdfGenerationException("Error generating PDF", "PDF_GENERATION_ERROR", e.getMessage());
        }
    }

    // Generate HTML from InvoiceData
    private String generateHtml(InvoiceData invoiceData) {
        try {
            log.info("Generating HTML from InvoiceData");
            Context context = new Context();
            context.setVariable("seller", invoiceData.getSeller());
            context.setVariable("sellerAddress", invoiceData.getSellerAddress());
            context.setVariable("sellerGstin", invoiceData.getSellerGstin());
            context.setVariable("buyer", invoiceData.getBuyer());
            context.setVariable("buyerAddress", invoiceData.getBuyerAddress());
            context.setVariable("buyerGstin", invoiceData.getBuyerGstin());
            context.setVariable("items", invoiceData.getItems());
            return templateEngine.process("invoice", context);
        } catch (Exception e) {
            log.error("Error generating HTML from InvoiceData: " + e.getMessage());
            throw new PdfGenerationException("Error generating HTML from InvoiceData", "HTML_GENERATION_ERROR", e.getMessage());
        }
    }

    // Generate a PDF from HTML content
    private byte[] generatePdfFromHtml(String htmlContent) {
        try {
            log.info("Generating PDF from HTML content");
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error generating PDF from HTML content: " + e.getMessage());
            throw new PdfGenerationException("Error generating PDF from HTML content", "PDF_GENERATION_ERROR", e.getMessage());
        }
    }
}