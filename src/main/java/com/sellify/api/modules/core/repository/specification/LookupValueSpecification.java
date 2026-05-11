package com.sellify.api.modules.core.repository.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.sellify.api.modules.core.domain.LookupValue;

import jakarta.persistence.criteria.Predicate;

public class LookupValueSpecification {

    public static Specification<LookupValue> byLookupGroup(String lookupGroupId) {
        return (root, query, cb) -> lookupGroupId == null ? null : cb.equal(root.get("lookupGroupId"), lookupGroupId);
    }

    public static Specification<LookupValue> isActive(Boolean active) {
        return (root, query, cb) -> active == null ? null : cb.equal(root.get("active"), active);
    }

    public static Specification<LookupValue> search(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) {
                return null;
            }

            String[] tokens = search
                .toLowerCase()
                .trim()
                .split("\\s+");

            List<Predicate> andPredicates = new ArrayList<>();
            for (String token : tokens) {
                String pattern = "%" + token + "%";
                Predicate tokenPredicate = cb.or(
                    cb.like(cb.lower(root.get("code")), pattern),
                    cb.like(cb.lower(root.get("name")), pattern)
                );
                andPredicates.add(tokenPredicate);
            }

            return cb.and(andPredicates.toArray(new Predicate[0]));
        };
    }
    
}
