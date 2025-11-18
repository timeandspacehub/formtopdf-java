package com.timeandspacehub.formtopdf.services;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.timeandspacehub.formtopdf.dto.BuyerInput;
import com.timeandspacehub.formtopdf.dto.PdfFieldStructure;

@Service
public class PdfFormExtractorService {

    public Set<String> extractFormFieldNames() throws Exception {
        ClassPathResource resource = new ClassPathResource("one-to-four.pdf");
        String pdfPath = resource.getFile().getAbsolutePath();

        Set<String> fieldNames = new LinkedHashSet<>();

        PDDocument document = PDDocument.load(new File(pdfPath));
        PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();

        if (acroForm != null) {
            List<PDField> fields = acroForm.getFields();
            for (PDField field : fields) {

                String rawStr = field.getFullyQualifiedName();
                String rawStrAndUnderscore = rawStr.replaceAll("\\s+", "_");
                rawStrAndUnderscore = rawStrAndUnderscore.replace('-', '_');

                fieldNames.add("public String var_" + rawStrAndUnderscore.toLowerCase() + ";");
            }
        }

        document.close();
        return fieldNames;
    }

    public List<PdfFieldStructure> getFieldInfo() throws Exception {

        List<PdfFieldStructure> list = new ArrayList<>();

        ClassPathResource resource = new ClassPathResource("one-to-four.pdf");
        String pdfPath = resource.getFile().getAbsolutePath();

        PDDocument document = PDDocument.load(new File(pdfPath));
        PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();

        if (acroForm != null) {
            List<PDField> fields = acroForm.getFields();
            System.out.println("Total no. of fields is" + fields.size());

            for (int i = 0; i < fields.size(); i++) {
                PDField field = fields.get(i);

                // PdfFieldStructure.b
                PdfFieldStructure obj = new PdfFieldStructure(i + 1, field.getFullyQualifiedName(),
                        field.getValueAsString(), field.getClass().getSimpleName());
                list.add(obj);
            }
        }

        document.close();

        return list;
    }

    public String fillAndSavePdf(BuyerInput input) throws Exception {
        // 1. Get PDF file from resources directory
        ClassPathResource resource = new ClassPathResource("one-to-four.pdf");
        String pdfPath = resource.getFile().getAbsolutePath();

        PDDocument document = PDDocument.load(new File(pdfPath));
        PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
        String originalDA = acroForm.getDefaultAppearance(); // For switching Helvetica font and Original font of Acro

        if (acroForm != null) {
            acroForm.setNeedAppearances(false);

            // 2. Fill only text fields first
            List<PdfFieldStructure> pdfFileStructureList = getFieldInfo();

            acroForm.setDefaultAppearance("/Helv 0 Tf 0 g");

            for (int i = 0; i < 255; i++) {

                PdfFieldStructure fieldDto = pdfFileStructureList.get(i);

                if (fieldDto.getFieldClass().equalsIgnoreCase("PDTextField")) {

                    String fieldName = fieldDto.getFieldName();
                    PDField field = acroForm.getField(fieldName);
                    field.getCOSObject().removeItem(COSName.DA);
                    
                    String rawStr = field.getFullyQualifiedName();
                    String methodName = rawStr.replaceAll("\\s+", "_");
                    methodName = "getVar_" + methodName.replace('-', '_').toLowerCase();

                    if("getVar_received_by_3".equalsIgnoreCase(methodName)){
                        System.out.println("YES");
                    }

                    // Get method on BuyerInput
                    Method method = BuyerInput.class.getMethod(methodName);

                    // Invoke method
                    Object value = method.invoke(input);

                    if (value !=null){
                       String x =  (String) value;
                       field.setValue(x);
                    }

                    acroForm.refreshAppearances(Arrays.asList(field));
                }
            }

            // 3. Fill only check boxes
            PDField rawField12 = acroForm.getField("B Sum of all financing described in the attached");

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
            System.err.println("Error: The PDF does not contain an AcroForm.");
        }

        String outputPath = "result.pdf";
        document.save(outputPath);
        document.close();

        return outputPath;
    }

    /*
    public String fillAndSavePdf(BuyerInput input) throws Exception {
        // 1. Get PDF file from resources directory
        ClassPathResource resource = new ClassPathResource("one-to-four.pdf");
        String pdfPath = resource.getFile().getAbsolutePath();

        PDDocument document = PDDocument.load(new File(pdfPath));
        PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
        String originalDA = acroForm.getDefaultAppearance(); // For switching Helvetica font and Original font of Acro

        if (acroForm != null) {
            acroForm.setNeedAppearances(false);

            // 2. Fill only text fields first
            List<PdfFieldStructure> pdfFileStructureList = getFieldInfo();

            acroForm.setDefaultAppearance("/Helv 0 Tf 0 g");
            for (int i = 0; i < 3; i++) {

                PdfFieldStructure fieldDto = pdfFileStructureList.get(i);

                if (fieldDto.getFieldClass().equalsIgnoreCase("PDTextField")) {


                    String fieldName = fieldDto.getFieldName();
                    PDField field = acroForm.getField(fieldName);
                    field.getCOSObject().removeItem(COSName.DA);
                    
    
                    // Build method name: "get" + "Apple"
                    //SAMPLE GETTER FOR INPUT OBJECT : getVAR_1_PARTIES_The_parties_to_this_contract_are
                    String methodName = "getVAR_" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    methodName = methodName.replaceAll("(?<=\\w) (?=\\w)", "_");

                    // Get method on BuyerInput
                    Method method = BuyerInput.class.getMethod(methodName);

                    // Invoke method
                    Object value = method.invoke(input);

                    if (value !=null){
                       String x =  (String) value;
                       field.setValue(x);
                    }

                    acroForm.refreshAppearances(Arrays.asList(field));
                }
            }

            // 3. Fill only check boxes
            PDField rawField12 = acroForm.getField("B Sum of all financing described in the attached");

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
            System.err.println("Error: The PDF does not contain an AcroForm.");
        }

        String outputPath = "result.pdf";
        document.save(outputPath);
        document.close();

        return outputPath;
    }
    */

}