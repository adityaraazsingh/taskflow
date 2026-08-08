package com.projectManagement.taskflow.entity;

import com.projectManagement.taskflow.enums.RoleInProject;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class ProjectMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private ProjectEntity project;

    @ManyToOne
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    private RoleInProject roleInProject;

    @Override
    public String toString() {
        return "ProjectMember{" +
                "id=" + id +
                ", project=" + project.getId() +
                ", user=" + user.getId() +
                ", roleInProject=" + roleInProject +
                '}';
    }
}