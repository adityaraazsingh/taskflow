package com.projectManagement.taskflow.notification;

import com.projectManagement.taskflow.tenant.TenantContext;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class Notification {
    private final SimpMessagingTemplate messagingTemplate;

    public Notification(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @RabbitListener(queues = "member.notification.queue")
    public void handleMemberChange(
            NotificationEvent event) {

        try {
            TenantContext.setTenant(event.tenantSchema());

            System.out.println("Member event: " + event);

            messagingTemplate.convertAndSend(
                    "/topic/activity",
                    event
            );

        } finally {
            TenantContext.clear();
        }
    }
}
