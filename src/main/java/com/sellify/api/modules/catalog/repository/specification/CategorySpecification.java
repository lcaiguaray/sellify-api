package com.sellify.api.modules.catalog.repository.specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.sellify.api.modules.catalog.domain.Category;

import jakarta.persistence.criteria.Predicate;

public class CategorySpecification {

    public static Specification<Category> byCompanyId(UUID companyId) {
        return (root, query, cb) -> companyId == null ? null : cb.equal(root.get("companyId"), companyId);
    }

    public static Specification<Category> byParentId(UUID categoryId) {
        return (root, query, cb) -> {
            if (categoryId == null) {
                return cb.isNull(root.get("parentId"));
            }
            return cb.equal(root.get("parentId"), categoryId);
        };
    }

    public static Specification<Category> isActive(Boolean active) {
        return (root, query, cb) -> active == null ? null : cb.equal(root.get("active"), active);
    }

    public static Specification<Category> search(String search) {
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
                Predicate tokenPredicate = cb.or(cb.like(cb.lower(root.get("name")), pattern));
                andPredicates.add(tokenPredicate);
            }

            return cb.and(andPredicates.toArray(new Predicate[0]));
        };
    }

}
