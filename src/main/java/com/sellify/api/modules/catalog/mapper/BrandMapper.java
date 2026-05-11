package com.sellify.api.modules.catalog.mapper;

import org.springframework.stereotype.Component;

import com.sellify.api.modules.catalog.domain.Brand;
import com.sellify.api.modules.catalog.dto.BrandResponse;

@Component
public class BrandMapper {

    public BrandResponse toResponse(Brand entity) {
        if (entity == null) {
            return null;
        }

        return new BrandResponse(
            entity.getId(),
            entity.getName(),
            entity.getSlug(),
            entity.getDescription(),
            entity.getLogoUrl(),
            entity.getActive()
        );
    }

}
