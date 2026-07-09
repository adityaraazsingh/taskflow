package com.projectManagement.taskflow.repository;

import com.projectManagement.taskflow.entity.ProjectEntity;
import com.projectManagement.taskflow.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepo extends JpaRepository<ProjectEntity,Long> {
    List<ProjectEntity> findByUser_id(Long id);

    List<ProjectMember> findAllById(Long projectId);
}
