package com.timeandspacehub.formtopdf.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.timeandspacehub.formtopdf.dto.BuyerInfoDto;
import com.timeandspacehub.formtopdf.dto.PdfFieldStructure;
import com.timeandspacehub.formtopdf.services.PdfFormExtractorService;

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


    @GetMapping("/field-info")
    public ResponseEntity<List<PdfFieldStructure>> getFieldInfo() throws Exception {
        List<PdfFieldStructure> list = pdfFormExtractorService.getFieldInfo();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/fill-and-save")
    public ResponseEntity<?> fillAndSavePdf(@RequestBody BuyerInfoDto input) {
        try {

            String savedFilePath = pdfFormExtractorService.fillAndSavePdf(input);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "PDF filled and saved successfully");
            response.put("filePath", savedFilePath);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/fields")
    public ResponseEntity<Set<String>> extractFormFields() throws Exception{
        Set<String> result = pdfFormExtractorService.extractFormFieldNames();
        return ResponseEntity.ok(result);
    }

}