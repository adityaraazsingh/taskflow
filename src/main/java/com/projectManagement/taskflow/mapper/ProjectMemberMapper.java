package com.projectManagement.taskflow.mapper;

import com.projectManagement.taskflow.dto.ProjectMemberResponseDto;
import com.projectManagement.taskflow.dto.ProjectRequestDto;
import com.projectManagement.taskflow.entity.ProjectMember;
import org.springframework.stereotype.Component;

@Component
public class ProjectMemberMapper {
   public ProjectMemberResponseDto toDto(ProjectMember entity){
       ProjectMemberResponseDto dto = new ProjectMemberResponseDto();
       dto.setId(entity.getId());
       dto.setProjectId(entity.getProject().getId());
       dto.setRoleInProject(entity.getRoleInProject());
       return dto;
   }

   public ProjectMember toEntity(ProjectRequestDto dto){
       ProjectMember entity = new ProjectMember();
       return entity;
   }
}
