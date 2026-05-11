package com.sellify.api.modules.catalog.mapper;

import org.springframework.stereotype.Component;

import com.sellify.api.modules.catalog.domain.Category;
import com.sellify.api.modules.catalog.dto.CategoryResponse;

@Component
public class CategoryMapper {

    public CategoryResponse toResponse(Category entity) {
        if (entity == null) {
            return null;
        }

        return new CategoryResponse(
            entity.getId(),
            entity.getParentId(),
            entity.getName(),
            entity.getSlug(),
            entity.getDescription(),
            entity.getActive()
        );
    }

}
