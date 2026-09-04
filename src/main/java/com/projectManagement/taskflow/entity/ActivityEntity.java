package com.projectManagement.taskflow.entity;

import com.projectManagement.taskflow.notification.NotificationEventEnum;
import com.projectManagement.taskflow.tenant.TenantContext;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class ActivityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private NotificationEventEnum eventName;

    private String tenantName;

    private String data;

    private Long projectId;

    private Long taskId;

    private Date createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = new Date();
        this.tenantName = TenantContext.getTenant();
    }

}
