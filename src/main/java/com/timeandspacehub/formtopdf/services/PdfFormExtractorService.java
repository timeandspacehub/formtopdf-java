package com.timeandspacehub.formtopdf.services;


import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
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

            acroForm.setNeedAppearances(true);

            // Get the specific fields by their exact names
            PDField field1 = acroForm.getField("1 PARTIES The parties to this contract are");
            PDField field2 = acroForm.getField("Seller and");

            if (field1 != null && field2 != null) {
                acroForm.setNeedAppearances(true);
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

                acroForm.flatten();
                System.out.println("Form fields filled successfully.");
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
    
    /**
    public String fillAndSavePdf(String sellerName, String buyerName) throws IOException {
        // Load the PDF template from the classpath
        ClassPathResource resource = new ClassPathResource("one-to-four.pdf");
        
        PDDocument document = null;
        try (InputStream is = resource.getInputStream()) {
            document = PDDocument.load(is);
            PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();

            if (acroForm != null) {
                
                acroForm.setNeedAppearances(true);


                // Get the specific fields by their exact names
                PDField field1 = acroForm.getField("1 PARTIES The parties to this contract are");
                PDField field2 = acroForm.getField("Seller and");

                if (field1 != null && field2 != null) {
                     acroForm.setNeedAppearances(true);
                    // --- NEW FIX ---
                    // The error is specific to the field's *existing* appearance string. 
                    // We clear it so PDFBox uses the global default we set (which is standard Helvetica) 
                    // or generates one from scratch correctly.
                    
                    // Clear the problematic default appearance from the field dictionary
                    field1.getCOSObject().removeItem(COSName.DA);
                    field2.getCOSObject().removeItem(COSName.DA);

                    // Set the global default appearance string to a known good value again, just in case
                    acroForm.setDefaultAppearance("/Helv 0 Tf 0 g");
                    // --- END NEW FIX ---

                    field1.setValue(buyerName);
                    // This refresh should now work without crashing because the problematic string is gone
                    acroForm.refreshAppearances(Arrays.asList(field1));

                    field2.setValue(sellerName);
                    acroForm.refreshAppearances(Arrays.asList(field2));

                    acroForm.flatten(); 
                    System.out.println("Form fields filled successfully.");
                } else {
                    System.err.println("Error: One or both specified form fields were not found in the PDF.");
                }
            } else {
                System.err.println("Error: The PDF does not contain an AcroForm.");
            }

            // Determine the output path relative to the resources directory
            File resourcesDirectory = ResourceUtils.getFile("classpath:");
            File outputDir = new File(resourcesDirectory.getAbsolutePath() + File.separator + "filled_forms");
            if (!outputDir.exists()) {
                outputDir.mkdirs(); // Create the directory if it doesn't exist
            }
            
            String outputFileName = "filled_" + "one-to-four.pdf";
            File outputFile = new File(outputDir, outputFileName);

            // Save the filled PDF
            try (OutputStream os = new FileOutputStream(outputFile)) {
                document.save(os);
            }
            
            return outputFile.getAbsolutePath();

        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (document != null) {
                document.close();
            }
        }
    }
    */
}