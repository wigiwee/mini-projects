package com.ZorvynFinanceApp.backend.models;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

public class RecordSpecification {

    public static Specification<Record> filter(String category,Double  minAmount,Double  maxAmount, String type) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (category != null) {
                predicates.add(cb.equal(root.get("category"), category));
            }
            if (minAmount != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("amount"), minAmount));
            }
            if (maxAmount != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("amount"), maxAmount));
            }
            if (type != null){
                predicates.add(cb.equal(root.get("type"), type));
            }

            if (predicates.isEmpty()){
                return null;
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
