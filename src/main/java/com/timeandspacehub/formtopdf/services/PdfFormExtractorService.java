package com.timeandspacehub.formtopdf.services;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Method;
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

    private final PdfCacheService pdfCacheService;


    public PdfFormExtractorService(PdfCacheService pdfCacheService) {
        this.pdfCacheService = pdfCacheService;
    }

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

    public int countDeclaredFields(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class cannot be null");
        }

        return clazz.getDeclaredFields().length;
    }

    public byte[] fillAndSavePdf(InputDto input) throws Exception {
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
            List<PdfFieldStructure> pdfFileStructureList = pdfCacheService.getFieldInfo();

            if (input.getBuyerInfoDto() != null) {
                Object obj = input.getBuyerInfoDto();
                writeToFile(0, buyerInfoDtoFieldsCt, originalDA, acroForm, obj, pdfFileStructureList);
            }

            if (input.getBrokerInfoDto() != null) {
                Object obj = input.getBrokerInfoDto();
                writeToFile(buyerInfoDtoFieldsCt, (buyerInfoDtoFieldsCt + brokerInfoDtoFieldsCt), originalDA, acroForm,
                        obj, pdfFileStructureList);
            }

            if (input.getReceiptInfoDto() != null) {
                Object obj = input.getReceiptInfoDto();
                writeToFile((buyerInfoDtoFieldsCt + brokerInfoDtoFieldsCt),
                        (buyerInfoDtoFieldsCt + brokerInfoDtoFieldsCt + receiptInfoDtoFieldsCt), originalDA, acroForm,
                        obj, pdfFileStructureList);
            }

        } else {
            System.err.println("Error: The PDF does not contain an AcroForm.");
        }

        // Use ByteArrayOutputStream to save to memory instead of disk
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        document.save(baos); 
        document.close();

        // Convert the in-memory data stream to a byte array
        return baos.toByteArray();
    }

    public Method getMethodNameDynamically(Object inputObj, String methodName) throws Exception {
        Method method = null;

        try {
            if (inputObj instanceof BuyerInfoDto) {
                method = BuyerInfoDto.class.getMethod(methodName);
            } else if (inputObj instanceof BrokerInfoDto) {
                method = BrokerInfoDto.class.getMethod(methodName);
            } else if (inputObj instanceof ReceiptInfoDto) {
                method = ReceiptInfoDto.class.getMethod(methodName);
            }
        } catch (Exception e) {
            System.out.println(methodName + " was not found");
        }

        return method;
    }

    public Object getFieldValueFromAPICall(Method method, Object inputObj) throws Exception {
        Object value = null;

        if (inputObj instanceof BuyerInfoDto) {
            value = method.invoke((BuyerInfoDto) inputObj);
        } else if (inputObj instanceof BrokerInfoDto) {
            value = method.invoke((BrokerInfoDto) inputObj);
        } else if (inputObj instanceof ReceiptInfoDto) {
            value = method.invoke((ReceiptInfoDto) inputObj);
        }
        return value;
    }

    public void writeToFile(int startIndex, int endIndex, String originalDA, PDAcroForm acroForm, Object inputObj,
            List<PdfFieldStructure> pdfFileStructureList) throws Exception {

        for (int i = startIndex; i < endIndex; i++) {

            PdfFieldStructure fieldDto = pdfFileStructureList.get(i);
            String fieldName = fieldDto.getFieldName();
            PDField field = acroForm.getField(fieldName);
            String methodName = fieldName.replaceAll("\\s+", "_");
            methodName = "getVar_" + methodName.replace('-', '_').toLowerCase();
            Method method = getMethodNameDynamically(inputObj, methodName);

            if (method == null) {
                System.out.println(methodName + " was not found when i = " + i);
                continue;
            }

            if (fieldDto.getFieldClass().equalsIgnoreCase("PDTextField")) {
                acroForm.setDefaultAppearance("/Helv 0 Tf 0 g");
                field.getCOSObject().removeItem(COSName.DA);

                Object value = getFieldValueFromAPICall(method, inputObj);

                if (value != null) {
                    String x = (String) value;
                    field.setValue(x);
                }

                acroForm.refreshAppearances(Arrays.asList(field));
            } else if (fieldDto.getFieldClass().equalsIgnoreCase("PDCheckBox")) {
                // Invoke method

                Object value = getFieldValueFromAPICall(method, inputObj);

                if (value != null) {
                    String yesNoString = (String) value;

                    if (!yesNoString.trim().isEmpty() && yesNoString.trim().equalsIgnoreCase("Y")) {

                        acroForm.setDefaultAppearance(originalDA);
                        PDCheckBox checkbox = (PDCheckBox) field;

                        checkbox.check();
                        acroForm.refreshAppearances(Arrays.asList(checkbox));
                    }

                }

            }
        }

    }

}