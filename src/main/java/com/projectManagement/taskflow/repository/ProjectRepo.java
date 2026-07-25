package com.projectManagement.taskflow.repository;

import com.projectManagement.taskflow.entity.ProjectEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepo extends JpaRepository<ProjectEntity,Long>, JpaSpecificationExecutor<ProjectEntity> {
    Page<ProjectEntity> findByUser_id(Long id, Specification<ProjectEntity> spec, Pageable pageable);
}
