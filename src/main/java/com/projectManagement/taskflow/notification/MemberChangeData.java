package com.projectManagement.taskflow.notification;

public record MemberChangeData(
        Long memberId,
        String memberName,
        Long projectId,
        String projectTitle
){}
