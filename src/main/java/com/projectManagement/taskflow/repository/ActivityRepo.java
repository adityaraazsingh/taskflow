package com.projectManagement.taskflow.repository;

import com.projectManagement.taskflow.entity.ActivityEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepo extends JpaRepository<ActivityEntity, Long> {
}
