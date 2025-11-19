package com.timeandspacehub.formtopdf.services;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
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

import com.timeandspacehub.formtopdf.dto.BrokerInfoDto;
import com.timeandspacehub.formtopdf.dto.BuyerInfoDto;
import com.timeandspacehub.formtopdf.dto.InputDto;
import com.timeandspacehub.formtopdf.dto.PdfFieldStructure;
import com.timeandspacehub.formtopdf.dto.ReceiptInfoDto;

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

    public int countDeclaredFields(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class cannot be null");
        }

        return clazz.getDeclaredFields().length;
    }

    public String fillAndSavePdf(InputDto input) throws Exception {
        // Cache this information
        int buyerInfoDtoFieldsCt = countDeclaredFields(BuyerInfoDto.class); // 0 to 177 (non-inclusive)
        int brokerInfoDtoFieldsCt = countDeclaredFields(BrokerInfoDto.class); // 177 to 225 (non-inclusive)
        int receiptInfoDtoFieldsCt = countDeclaredFields(ReceiptInfoDto.class); // 225 to 263 (non-inclusive)

        // 1. Get PDF file from resources directory
        ClassPathResource resource = new ClassPathResource("one-to-four.pdf");
        String pdfPath = resource.getFile().getAbsolutePath();

        PDDocument document = PDDocument.load(new File(pdfPath));
        PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
        String originalDA = acroForm.getDefaultAppearance(); // For switching Helvetica font and Original font of Acro

        if (acroForm != null) {
            acroForm.setNeedAppearances(false);

            // 2. Lazy Instantiation
            List<PdfFieldStructure> pdfFileStructureList = getFieldInfo();

            if(input.getBuyerInfoDto() != null){
                for (int i = 0; i < buyerInfoDtoFieldsCt; i++) {

                    PdfFieldStructure fieldDto = pdfFileStructureList.get(i);
                    String fieldName = fieldDto.getFieldName();
                    PDField field = acroForm.getField(fieldName);
                    String methodName = fieldName.replaceAll("\\s+", "_");
                    methodName = "getVar_" + methodName.replace('-', '_').toLowerCase();
                    Method method = BuyerInfoDto.class.getMethod(methodName);

                    if (fieldDto.getFieldClass().equalsIgnoreCase("PDTextField")) {
                        acroForm.setDefaultAppearance("/Helv 0 Tf 0 g");
                        field.getCOSObject().removeItem(COSName.DA);

                        // Invoke method
                        Object value = method.invoke(input.getBuyerInfoDto());

                        if (value != null) {
                            String x = (String) value;
                            field.setValue(x);
                        }

                        acroForm.refreshAppearances(Arrays.asList(field));
                    }else if (fieldDto.getFieldClass().equalsIgnoreCase("PDCheckBox")){
                        // Invoke method
                        Object value = method.invoke(input.getBuyerInfoDto());

                        if (value != null) {
                            String yesNoString = (String) value;

                            if (!yesNoString.trim().isEmpty() && yesNoString.trim().equalsIgnoreCase("Y")){

                                acroForm.setDefaultAppearance(originalDA);
                                PDCheckBox checkbox = (PDCheckBox) field;

                                checkbox.check();
                                acroForm.refreshAppearances(Arrays.asList(checkbox));
                            }
               
                        }

                        
                    }
                }

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