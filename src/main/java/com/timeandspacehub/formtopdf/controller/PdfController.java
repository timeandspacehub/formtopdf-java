package com.timeandspacehub.formtopdf.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.timeandspacehub.formtopdf.services.PdfFormExtractorService;

import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {


    @Autowired
    private PdfFormExtractorService pdfFormExtractorService;

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

    @GetMapping("/data")
    public String getData(){
        return "helo";
    }


    @PostMapping("/fill-and-save")
    public ResponseEntity<?> fillAndSavePdf(@RequestBody Map<String, String> data) {
        try {
            String sellerName = data.get("sellerName");
            String buyerName = data.get("buyerName");
            
            if (sellerName == null || sellerName.trim().isEmpty()) {
                throw new IllegalArgumentException("sellerName is required");
            }
            if (buyerName == null || buyerName.trim().isEmpty()) {
                throw new IllegalArgumentException("buyerName is required");
            }
            
            String savedFilePath = pdfFormExtractorService.fillAndSavePdf(sellerName, buyerName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "PDF filled and saved successfully");
            response.put("filePath", savedFilePath);
            response.put("sellerName", sellerName);
            response.put("buyerName", buyerName);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }






}