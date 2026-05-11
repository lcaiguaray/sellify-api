package com.sellify.api.modules.core.mapper;

import org.springframework.stereotype.Component;

import com.sellify.api.modules.core.domain.LookupGroup;
import com.sellify.api.modules.core.dto.LookupGroupResponse;

@Component
public class LookupGroupMapper {

    public LookupGroupResponse toResponse(LookupGroup entity) {
        if (entity == null) {
            return null;
        }

        return new LookupGroupResponse(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getActive()
        );
    }

}
