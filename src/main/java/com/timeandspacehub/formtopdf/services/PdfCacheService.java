package com.timeandspacehub.formtopdf.services;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.timeandspacehub.formtopdf.dto.PdfFieldStructure;

@Service
public class PdfCacheService {

    @Cacheable("pdfFieldStructures") // Use a clear cache name
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
}