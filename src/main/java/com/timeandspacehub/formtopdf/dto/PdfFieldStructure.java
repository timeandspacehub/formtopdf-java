package com.timeandspacehub.formtopdf.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PdfFieldStructure {
    int fieldId;
    String fieldName;
    String defaultValue;
    String fieldClass;
}

