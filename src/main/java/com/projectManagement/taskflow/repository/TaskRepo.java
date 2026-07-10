package com.projectManagement.taskflow.repository;

import com.projectManagement.taskflow.entity.TaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepo extends JpaRepository<TaskEntity,Long> {
    Page<TaskEntity> findByProject_id(Long id, Pageable pageable);
    List<TaskEntity> findByProject_id(Long id);

}
