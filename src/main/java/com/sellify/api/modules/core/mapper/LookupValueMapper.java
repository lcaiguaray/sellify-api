package com.sellify.api.modules.core.mapper;

import org.springframework.stereotype.Component;

import com.sellify.api.modules.core.domain.LookupValue;
import com.sellify.api.modules.core.dto.LookupValueResponse;

@Component
public class LookupValueMapper {

    public LookupValueResponse toResponse(LookupValue entity) {
        if (entity == null) {
            return null;
        }

        return new LookupValueResponse(
            entity.getId(),
            entity.getLookupGroupId(),
            entity.getCode(),
            entity.getName(),
            entity.getDescription(),
            entity.getActive()
        );
    }

}
