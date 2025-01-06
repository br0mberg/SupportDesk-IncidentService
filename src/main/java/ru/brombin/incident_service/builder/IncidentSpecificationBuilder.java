package ru.brombin.incident_service.builder;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.brombin.incident_service.dto.IncidentFilterDto;
import ru.brombin.incident_service.entity.*;

import java.util.ArrayList;
import java.util.List;

@Component
public class IncidentSpecificationBuilder {

    public Specification<Incident> build(IncidentFilterDto filterDto) {
        return (root, query, cb) -> {
            List<Predicate> criteriaPredicates = new ArrayList<>();

            addPredicateIfNotNull(criteriaPredicates, cb, root.get(Incident_.status), filterDto.status());
            addPredicateIfNotNull(criteriaPredicates, cb, root.get(Incident_.category), filterDto.category());
            addPredicateIfNotNull(criteriaPredicates, cb, root.get(Incident_.priority), filterDto.priority());
            addPredicateIfNotNull(criteriaPredicates, cb, root.get(Incident_.responsibleService), filterDto.responsibleService());

            if (filterDto.fromDate() != null && filterDto.toDate() != null) {
                criteriaPredicates.add(cb.between(root.get(Incident_.dateCreate), filterDto.fromDate(), filterDto.toDate()));
            }

            return cb.and(criteriaPredicates.toArray(new Predicate[0]));
        };
    }

    private <T> void addPredicateIfNotNull(List<Predicate> predicates,
                                           CriteriaBuilder cb, Path<T> field, T value) {
        if (value != null) {
            predicates.add(cb.equal(field, value));
        }
    }
}