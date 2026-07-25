package com.projectManagement.taskflow.filter;

import com.projectManagement.taskflow.entity.ProjectEntity;
import com.projectManagement.taskflow.enums.Priority;
import com.projectManagement.taskflow.enums.Status;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProjectSpecification {

    public static Specification<ProjectEntity> filterProjects(
            Long userId,
            Status status,
            Priority priority,
            String name,
            boolean isAdmin) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // 👇 Only restrict user if NOT admin
            if (!isAdmin) {
                predicates.add(cb.equal(root.get("user").get("id"), userId));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (priority != null) {
                predicates.add(cb.equal(root.get("priority"), priority));
            }

            if (name != null) {
                predicates.add(cb.like(
                        cb.lower(root.get("name")),
                        "%" + name.toLowerCase() + "%"
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}