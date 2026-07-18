package com.projectManagement.taskflow.repository;

import com.projectManagement.taskflow.entity.ProjectEntity;
import com.projectManagement.taskflow.entity.ProjectMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepo extends JpaRepository<ProjectEntity,Long> {
    Page<ProjectEntity> findByUser_id(Long id, Pageable pageable);

    List<ProjectMember> findAllById(Long projectId);
    Optional<ProjectEntity> findAllByProject_id(Long projectId);
}
