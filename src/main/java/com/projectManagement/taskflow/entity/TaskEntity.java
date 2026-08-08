package com.projectManagement.taskflow.entity;

import com.projectManagement.taskflow.enums.Status;
import com.projectManagement.taskflow.enums.Priority;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @JoinColumn(name = "assignee_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity assignee;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentEntity> comments = new ArrayList<>();

    @JoinColumn(name = "project_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ProjectEntity project;

    @ManyToMany
    private List<TagEntity> tags = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    private Date dueDate;

    private Date createdAt;

    private Date updatedAt;

    @Override
    public String toString() {
        return "TaskEntity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", assignee=" + assignee.getId() +
                ", project=" + project.getId() +
                ", status=" + status +
                ", priority=" + priority +
                ", dueDate=" + dueDate +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}