package com.timeandspacehub.formtopdf.services;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class PdfFormExtractorService {
    
    public List<String> extractFormFieldNames(String pdfPath) throws Exception {
        List<String> fieldNames = new ArrayList<>();
        
        PDDocument document = PDDocument.load(new File(pdfPath));
        PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
        
        if (acroForm != null) {
            List<PDField> fields = acroForm.getFields();
            for (PDField field : fields) {
                fieldNames.add(field.getFullyQualifiedName());
            }
        }
        
        document.close();
        return fieldNames;
    }
    
    public List<String> extractFormFields() throws Exception {
        ClassPathResource resource = new ClassPathResource("one-to-four.pdf");
        String pdfPath = resource.getFile().getAbsolutePath();
        return extractFormFieldNames(pdfPath);
    }
    
    public void debugFieldInfo() throws Exception {
        ClassPathResource resource = new ClassPathResource("one-to-four.pdf");
        String pdfPath = resource.getFile().getAbsolutePath();
        
        PDDocument document = PDDocument.load(new File(pdfPath));
        PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
        
        if (acroForm != null) {
            List<PDField> fields = acroForm.getFields();
            System.out.println("\n=== FIRST 5 FIELDS DEBUG INFO ===\n");
            
            for (int i = 0; i < Math.min(5, fields.size()); i++) {
                PDField field = fields.get(i);
                System.out.println("Field " + i + ":");
                System.out.println("  Name: " + field.getFullyQualifiedName());
                System.out.println("  Value: " + field.getValueAsString());
                System.out.println("  Type: " + field.getClass().getSimpleName());
                System.out.println();
            }
        }
        
        document.close();
    }
    
    public String fillAndSavePdf(String sellerName, String buyerName) throws Exception {
        ClassPathResource resource = new ClassPathResource("one-to-four.pdf");
        String pdfPath = resource.getFile().getAbsolutePath();
        
        PDDocument document = PDDocument.load(new File(pdfPath));
        PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
        
        if (acroForm != null) {
            PDField sellerField = acroForm.getField("1 PARTIES The parties to this contract are");
            PDField buyerField = acroForm.getField("Seller and");
            
            if (sellerField != null) {
                sellerField.setValue(sellerName);
            }
            
            if (buyerField != null) {
                buyerField.setValue(buyerName);
            }
        }
        
        String outputPath = "result.pdf";
        document.save(outputPath);
        document.close();
        
        return outputPath;
    }
}