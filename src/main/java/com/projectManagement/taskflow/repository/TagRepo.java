package com.projectManagement.taskflow.repository;

import com.projectManagement.taskflow.entity.TagEntity;
import com.projectManagement.taskflow.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepo extends JpaRepository<TagEntity,Long> {
}
