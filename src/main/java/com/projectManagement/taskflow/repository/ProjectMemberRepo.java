package com.projectManagement.taskflow.repository;

import com.projectManagement.taskflow.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectMemberRepo extends JpaRepository<ProjectMember, Long> {
    ProjectMember findByUser_idAndProject_id(Long userId, Long projectId);

    Optional<ProjectMember> findAllByProject_id(Long projectId);
}
