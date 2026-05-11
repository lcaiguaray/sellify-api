package com.sellify.api.modules.catalog.dto;

import java.math.BigDecimal;

import com.sellify.api.modules.core.dto.LookupValueResponse;

public record ProductResponse(
    CategoryResponse categoryResponse,
    BrandResponse brandResponse,
    LookupValueResponse uom,
    LookupValueResponse productType,
    String name,
    String slug,
    String description,
    BigDecimal minPrice,
    boolean hasVariant,
    boolean isTaxable,
    Long variantCount
) {}