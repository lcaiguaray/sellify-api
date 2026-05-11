package com.sellify.api.modules.catalog.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.sellify.api.common.validation.ValidSlug;

import jakarta.validation.constraints.NotBlank;

public record ProductRequest(
    @NotBlank(message = "{validation.notblank}")
    UUID categoryId,
    UUID brandId,
    @NotBlank(message = "{validation.notblank}")
    UUID uomId,
    @NotBlank(message = "{validation.notblank}")
    UUID productTypeId,
    @NotBlank(message = "{validation.notblank}")
    String name,
    @ValidSlug()
    @NotBlank(message = "{validation.notblank}")
    String slug,
    String description,
    BigDecimal price,
    boolean hasVariant,
    boolean isTaxable,
    List<VariantRequest> variants
) {
    public record VariantRequest(
        @NotBlank(message = "{validation.notblank}")
        String sku,
        @NotBlank(message = "{validation.notblank}")
        BigDecimal salePrice,
        BigDecimal costPrice,
        Map<String, Object> attributes
    ) {}
}