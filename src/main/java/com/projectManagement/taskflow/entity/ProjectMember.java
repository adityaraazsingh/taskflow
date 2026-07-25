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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProjectEntity getProject() {
        return project;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public RoleInProject getRoleInProject() {
        return roleInProject;
    }

    public void setRoleInProject(RoleInProject roleInProject) {
        this.roleInProject = roleInProject;
    }

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