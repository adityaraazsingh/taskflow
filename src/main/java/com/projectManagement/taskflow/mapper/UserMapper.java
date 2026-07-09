package com.projectManagement.taskflow.mapper;

import com.projectManagement.taskflow.dto.UserRequestDTO;
import com.projectManagement.taskflow.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserEntity toEntity(UserRequestDTO dto, String encodedPassword) {
        UserEntity user = new UserEntity();

        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        user.setPasswordHash(encodedPassword);
        user.setCreatedAt(new java.util.Date());

        return user;
    }
}
