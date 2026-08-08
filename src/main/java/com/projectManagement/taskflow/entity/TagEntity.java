package com.projectManagement.taskflow.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class TagEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToMany(mappedBy = "tags")
    private List<TaskEntity> tasks = new ArrayList<>();

    private String name;

    private String colorHex;

    @Override
    public String toString() {
        return "TagEntity{" +
                "id=" + id +
                ", tasks=" + tasks +
                ", name='" + name + '\'' +
                ", colorHex='" + colorHex + '\'' +
                '}';
    }
}
