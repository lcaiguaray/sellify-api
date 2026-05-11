package com.sellify.api.modules.core.util;

import org.springframework.stereotype.Component;

import com.sellify.api.modules.core.domain.LookupValue;

@Component
public class DocumentTypeHelper {

    public boolean isLegalEntity(LookupValue docType, String docNumber) {
        if (docType == null || docNumber == null) return false;

        if (docType.getAttributes().containsKey("requires_tax_validation")
            && (Boolean) docType.getAttributes().get("requires_tax_validation")) {
            return docNumber.startsWith("20");
        }
        return false;
    }

}
