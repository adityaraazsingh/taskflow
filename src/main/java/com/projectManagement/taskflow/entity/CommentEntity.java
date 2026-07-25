package com.projectManagement.taskflow.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public TaskEntity getTask() {
        return task;
    }

    public void setTask(TaskEntity task) {
        this.task = task;
    }

    public UserEntity getCommentator() {
        return commentator;
    }

    public void setCommentator(UserEntity commentator) {
        this.commentator = commentator;
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
