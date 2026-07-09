package com.projectManagement.taskflow.repository;

import com.projectManagement.taskflow.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepo extends JpaRepository<TagEntity,Long> {
}
