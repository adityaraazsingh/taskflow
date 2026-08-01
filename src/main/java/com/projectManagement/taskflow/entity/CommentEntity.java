package com.projectManagement.taskflow.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String content;

    @JoinColumn(name = "task_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private TaskEntity task;

    @JoinColumn(name = "commentator_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity commentator;

    private Date createdAt;

    @PrePersist
    protected void createdAt(){
        createdAt = new Date();
    }

    @Override
    public String toString() {
        return "CommentEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", task=" + task.getId() +
                ", commentator=" + commentator.getId() +
                '}';
    }
}
