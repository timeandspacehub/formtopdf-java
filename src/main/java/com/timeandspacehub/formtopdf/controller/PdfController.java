package com.timeandspacehub.formtopdf.controller;

import java.util.ArrayList;
import java.util.List;
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
import com.timeandspacehub.formtopdf.entity.Buyer;
import com.timeandspacehub.formtopdf.request.CreateBuyerRequest;
import com.timeandspacehub.formtopdf.response.CreateBuyerResponse;
import com.timeandspacehub.formtopdf.services.BuyerService;
import com.timeandspacehub.formtopdf.services.PdfCacheService;
import com.timeandspacehub.formtopdf.services.PdfFormExtractorService;

@RestController
@RequestMapping("/api/")
public class PdfController {

    private PdfFormExtractorService pdfFormExtractorService;
    private PdfCacheService pdfCacheService;
    private BuyerService buyerService;

    public PdfController(PdfFormExtractorService pdfFormExtractorService, PdfCacheService pdfCacheService, 
        BuyerService buyerService
    ) {
        this.pdfFormExtractorService = pdfFormExtractorService;
        this.pdfCacheService = pdfCacheService;
        this.buyerService = buyerService;
    }

    @GetMapping("/field-info")
    public ResponseEntity<List<PdfFieldStructure>> getFieldInfo() throws Exception {
        List<PdfFieldStructure> list = pdfCacheService.getFieldInfo();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/pdf")
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

    @GetMapping("/buyers")
    public ResponseEntity<List<CreateBuyerResponse>> test() throws Exception {
        List<Buyer> buyersList = buyerService.getAllBuyers();
        List<CreateBuyerResponse> result = new ArrayList<>();

        for(int i=0; i<buyersList.size(); i++){
            Buyer buyer = buyersList.get(i);
            CreateBuyerResponse response = new CreateBuyerResponse(buyer);
            result.add(response);
        }
        
        return ResponseEntity.ok(result);
    }

    @PostMapping("/buyer")
    public ResponseEntity<CreateBuyerResponse> createBuyer(CreateBuyerRequest request) throws Exception {
        Buyer buyer = buyerService.createBuyer(request);
        CreateBuyerResponse response = new CreateBuyerResponse(buyer);
        return ResponseEntity.ok(response);
    }

}