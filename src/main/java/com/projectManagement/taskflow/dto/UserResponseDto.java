package com.projectManagement.taskflow.dto;
import com.projectManagement.taskflow.enums.RoleEnum;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class UserResponseDto {
    private Long id;
    private String username;
    private String email;
    private RoleEnum role;
    private List<Long> projectIds = new ArrayList<>();
    private List<Long> taskIds = new ArrayList<>();
    private List<Long> commentIds = new ArrayList<>();
    private List<Long> projectMemberIds = new ArrayList<>();
    private Date createdAt;
}
