package com.timeandspacehub.formtopdf.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.timeandspacehub.formtopdf.dto.InputDto;
import com.timeandspacehub.formtopdf.dto.PdfFieldStructure;
import com.timeandspacehub.formtopdf.services.PdfCacheService;
import com.timeandspacehub.formtopdf.services.PdfFormExtractorService;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {

    private PdfFormExtractorService pdfFormExtractorService;
    private final PdfCacheService pdfCacheService;

    public PdfController(PdfFormExtractorService pdfFormExtractorService, PdfCacheService pdfCacheService) {
        this.pdfFormExtractorService = pdfFormExtractorService;
        this.pdfCacheService = pdfCacheService;
    }

    @GetMapping("/generate")
    public ResponseEntity<byte[]> generatePdf() {
        // TODO: Add your PDF generation logic here
        byte[] pdfBytes = new byte[0];

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "document.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @GetMapping("/field-info")
    public ResponseEntity<List<PdfFieldStructure>> getFieldInfo() throws Exception {
        List<PdfFieldStructure> list = pdfCacheService.getFieldInfo();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/fill-and-save")
    public ResponseEntity<byte[]> fillAndSavePdf(@RequestBody InputDto input) {
        try {
            // Get the PDF data as a byte array
            byte[] pdfBytes = pdfFormExtractorService.fillAndSavePdf(input);

            // Set HTTP headers for file download and PDF content type
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "filled_form.pdf"); // Forces download
            headers.setContentLength(pdfBytes.length);

            // Return the byte array within a ResponseEntity
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            // Handle exceptions appropriately
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping("/fields")
    public ResponseEntity<Set<String>> extractFormFields() throws Exception {
        Set<String> result = pdfFormExtractorService.extractFormFieldNames();
        return ResponseEntity.ok(result);
    }

}