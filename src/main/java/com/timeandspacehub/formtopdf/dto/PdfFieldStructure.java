package com.timeandspacehub.formtopdf.dto;

public class PdfFieldStructure {
    int fieldId;
    String fieldName;
    String defaultValue;
    String fieldClass;

    public PdfFieldStructure(){}

    public PdfFieldStructure(int fieldId, String fieldName, String defaultValue, String fieldClass) {
        this.fieldId = fieldId;
        this.fieldName = fieldName;
        this.defaultValue = defaultValue;
        this.fieldClass = fieldClass;
    }
    public int getFieldId() {
        return fieldId;
    }
    public void setFieldId(int fieldId) {
        this.fieldId = fieldId;
    }
    public String getFieldName() {
        return fieldName;
    }
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
    public String getDefaultValue() {
        return defaultValue;
    }
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
    public String getFieldClass() {
        return fieldClass;
    }
    public void setFieldClass(String fieldClass) {
        this.fieldClass = fieldClass;
    }
}

