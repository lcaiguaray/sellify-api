package com.sellify.api.modules.core.repository.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.sellify.api.modules.core.domain.LookupGroup;

import jakarta.persistence.criteria.Predicate;

public class LookupGroupSpecification {

    public static Specification<LookupGroup> isActive(Boolean active) {
        return (root, query, cb) -> active == null ? null : cb.equal(root.get("active"), active);
    }

    public static Specification<LookupGroup> search(String search) {
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
                    cb.like(cb.lower(root.get("id")), pattern),
                    cb.like(cb.lower(root.get("name")), pattern)
                );
                andPredicates.add(tokenPredicate);
            }

            return cb.and(andPredicates.toArray(new Predicate[0]));
        };
    }

}