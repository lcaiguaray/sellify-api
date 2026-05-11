package com.sellify.api.common.enums;

import lombok.Getter;

@Getter
public enum LookupGroupCode {
    
    DocumentType("DOCUMENT_TYPE"),
    Gender("GENDER"),
    CivilStatus("CIVIL_STATUS"),
    EducationLevel("EDUCATION_LEVEL");

    private final String code;

    LookupGroupCode(String code) {
        this.code = code;
    }
}
