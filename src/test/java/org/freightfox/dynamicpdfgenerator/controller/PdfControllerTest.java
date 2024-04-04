package org.freightfox.dynamicpdfgenerator.controller;

import org.freightfox.dynamicpdfgenerator.dto.InvoiceData;
import org.freightfox.dynamicpdfgenerator.dto.Item;
import org.freightfox.dynamicpdfgenerator.exception.PdfGenerationException;
import org.freightfox.dynamicpdfgenerator.service.PdfService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springdoc.api.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PdfControllerTest {

    @Mock
    private PdfService pdfService;

    @InjectMocks
    private PdfController pdfController;

    @Test
    public void testGeneratePdf() throws Exception {
        // Create and populate InvoiceData
        InvoiceData invoiceData = new InvoiceData();
        invoiceData.setSeller("XYZ Pvt. Ltd.");
        invoiceData.setSellerGstin("29AABBCCDD121ZD");
        invoiceData.setSellerAddress("New Delhi, India");
        invoiceData.setBuyer("Vedant Computers");
        invoiceData.setBuyerGstin("29AABBCCDD131ZD");
        invoiceData.setBuyerAddress("New Delhi, India");

        List<Item> items = new ArrayList<>();
        Item item = new Item();
        item.setName("Product 1");
        item.setQuantity("12 Nos");
        item.setRate(123.00);
        item.setAmount(1476.00);
        items.add(item);

        invoiceData.setItems(items);

        File mockFile = new File("src/test/resources/mock.pdf"); // this should be a real file in your test resources
        byte[] mockFileBytes = Files.readAllBytes(mockFile.toPath());

        when(pdfService.generatePdf(invoiceData)).thenReturn(mockFile);

        ResponseEntity responseEntity = pdfController.generatePdf(invoiceData);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockFileBytes, responseEntity.getBody());
    }

    @Test
    public void testGeneratePdf_PdfGenerationException() throws Exception {
        // Create and populate InvoiceData
        InvoiceData invoiceData = new InvoiceData();
        invoiceData.setSeller("XYZ Pvt. Ltd.");

        when(pdfService.generatePdf(invoiceData)).thenThrow(new PdfGenerationException("Error generating PDF", "PDF_GENERATION_ERROR", "Invalid data"));

        ResponseEntity responseEntity = pdfController.generatePdf(invoiceData);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody() instanceof PdfGenerationException);
    }

    @Test
    public void testGeneratePdf_Exception() throws Exception {
        // Create and populate InvoiceData
        InvoiceData invoiceData = new InvoiceData();
        invoiceData.setSeller("XYZ Pvt. Ltd.");

        when(pdfService.generatePdf(invoiceData)).thenThrow(new Exception("Unexpected error"));

        ResponseEntity responseEntity = pdfController.generatePdf(invoiceData);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody() instanceof ErrorMessage);
    }

}