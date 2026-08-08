package com.projectManagement.taskflow.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projectManagement.taskflow.enums.RoleEnum;
import com.projectManagement.taskflow.mapper.ProjectMapper;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Entity
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @JsonIgnore
    private String passwordHash;

    private String email;

    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<ProjectEntity> projects = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "assignee")
    private List<TaskEntity> tasks = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "commentator")
    private List<CommentEntity> comment = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<ProjectMember> projectMembers = new ArrayList<>();

    private Date createdAt;

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", tasks=" + tasks +
                ", createdAt=" + createdAt +
                '}';
    }
}
