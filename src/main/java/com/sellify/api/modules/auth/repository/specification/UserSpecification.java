package com.sellify.api.modules.auth.repository.specification;

import com.sellify.api.modules.auth.domain.User;
import com.sellify.api.modules.auth.domain.UserCompany;
import com.sellify.api.modules.people.domain.Identity;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserSpecification {

    public static Specification<User> byCompanyId(UUID companyId) {
        return (root, query, cb) -> {
            if (companyId == null) {
                return null;
            }
            
            query.distinct(true);

            Subquery<UUID> subquery = query.subquery(UUID.class);
            Root<UserCompany> uc = subquery.from(UserCompany.class);

            subquery.select(uc.get("id").get("userId"))
                .where(cb.equal(uc.get("id").get("companyId"), companyId));

            return root.get("id").in(subquery);
        };
    }

    public static Specification<User> isActive(Boolean active) {
        return (root, query, cb) -> active == null ? null : cb.equal(root.get("active"), active);
    }

    public static Specification<User> search(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) {
                return null;
            }

            Join<User, Identity> identity = root.join("identity", JoinType.INNER);

            String[] tokens = search
                .toLowerCase()
                .trim()
                .split("\\s+");

            List<Predicate> andPredicates = new ArrayList<>();
            for (String token : tokens) {
                String pattern = "%" + token + "%";
                Predicate tokenPredicate = cb.or(
                    cb.like(cb.lower(root.get("username")), pattern),
                    cb.like(cb.lower(identity.get("documentNumber")), pattern),
                    cb.like(cb.lower(identity.get("firstName")), pattern),
                    cb.like(cb.lower(identity.get("lastName")), pattern),
                    cb.like(cb.lower(identity.get("business_name")), pattern),
                    cb.like(cb.lower(identity.get("trade_name")), pattern)
                );
                andPredicates.add(tokenPredicate);
            }

            return cb.and(andPredicates.toArray(new Predicate[0]));
        };
    }
}
