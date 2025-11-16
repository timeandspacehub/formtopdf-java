package com.timeandspacehub.formtopdf.services;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class PdfFormExtractorService {
    
    public List<String> extractFormFieldNames() throws Exception {
        ClassPathResource resource = new ClassPathResource("one-to-four.pdf");
        String pdfPath = resource.getFile().getAbsolutePath();

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
        
    public void debugFieldInfo() throws Exception {
        ClassPathResource resource = new ClassPathResource("one-to-four.pdf");
        String pdfPath = resource.getFile().getAbsolutePath();
        
        PDDocument document = PDDocument.load(new File(pdfPath));
        PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
        
        if (acroForm != null) {
            List<PDField> fields = acroForm.getFields();
            System.out.println("\n=== FIRST 5 FIELDS DEBUG INFO ===\n");
            
            for (int i = 0; i <= Math.min(15, fields.size()); i++) {
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
        String originalDA = acroForm.getDefaultAppearance(); //For switching Helvetica font and Original font of Acro
        
        if (acroForm != null) {

            acroForm.setNeedAppearances(false);

            // Get the specific fields by their exact names
            PDField field1 = acroForm.getField("1 PARTIES The parties to this contract are");
            PDField field2 = acroForm.getField("Seller and");
            PDField rawField12 = acroForm.getField("B Sum of all financing described in the attached");


            if (field1 != null && field2 != null && rawField12 !=null) {
                // --- NEW FIX ---
                // The error is specific to the field's *existing* appearance string.
                // We clear it so PDFBox uses the global default we set (which is standard
                // Helvetica)
                // or generates one from scratch correctly.

                // Clear the problematic default appearance from the field dictionary
                field1.getCOSObject().removeItem(COSName.DA);
                field2.getCOSObject().removeItem(COSName.DA);

                // Set the global default appearance string to a known good value again, just in
                // case
                acroForm.setDefaultAppearance("/Helv 0 Tf 0 g");
                // --- END NEW FIX ---

                field1.setValue(buyerName);
                // This refresh should now work without crashing because the problematic string
                // is gone
                acroForm.refreshAppearances(Arrays.asList(field1));

                field2.setValue(sellerName);
                acroForm.refreshAppearances(Arrays.asList(field2));

                // Checkbox code ony - Starts
                acroForm.setDefaultAppearance(originalDA);

                if (rawField12 instanceof PDCheckBox) {

                    PDCheckBox checkbox = (PDCheckBox) rawField12;
                  
                    checkbox.check();
                    acroForm.refreshAppearances(Arrays.asList(checkbox));
                } else {
                    System.err.println("Field 'B Sum of all financing described in the attached' is not a checkbox type!");
                }
                System.out.println("Form fields filled successfully.");
                // Checkbox code ony - Ends
                
            } else {
                System.err.println("Error: One or both specified form fields were not found in the PDF.");
            }
        } else {
            System.err.println("Error: The PDF does not contain an AcroForm.");
        }
        
        String outputPath = "result.pdf";
        document.save(outputPath);
        document.close();
        
        return outputPath;
    }
    
    
}