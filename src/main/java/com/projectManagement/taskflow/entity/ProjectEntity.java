package com.projectManagement.taskflow.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projectManagement.taskflow.enums.Status;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class ProjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    private UserEntity user;

    @JsonIgnore
    @OneToMany(mappedBy = "project")
    private List<TaskEntity> tasks = new ArrayList<>();;

    @JsonIgnore
    @OneToMany(mappedBy = "project")
    private List<ProjectMember> projectMembers = new ArrayList<>();;

    private Date CreatedAt;

    @Override
    public String toString() {
        return "ProjectEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", user=" + user.getId() +
                ", tasks=" + tasks +
                ", projectMembers=" + projectMembers +
                ", CreatedAt=" + CreatedAt +
                '}';
    }
}
