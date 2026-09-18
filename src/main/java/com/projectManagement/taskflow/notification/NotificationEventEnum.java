package com.projectManagement.taskflow.notification;

import lombok.Getter;

@Getter
public enum NotificationEventEnum {
    TASK_CREATED("task.exchange", "task.created"),
    TASK_UPDATED("task.exchange", "task.updated"),
    TASK_DELETED("task.exchange", "task.deleted"),

    PROJECT_CREATED("project.exchange", "project.created"),
    PROJECT_UPDATED("project.exchange", "project.updated"),
    PROJECT_DELETED("project.exchange", "project.deleted"),

    COMMENT_ADDED("comment.exchange", "comment.created"),
    COMMENT_DELETED("comment.exchange", "comment.deleted"),

    MEMBER_CHANGED("member.exchange", "member.changed"),
    MEMBER_DELETED("member.exchange", "member.deleted");

    private final String exchange;
    private final String routingKey;
    NotificationEventEnum(String exchange, String routingKey) {
        this.exchange = exchange;
        this.routingKey = routingKey;
    }
}
