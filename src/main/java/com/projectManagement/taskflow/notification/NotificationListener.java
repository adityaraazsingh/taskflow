package com.projectManagement.taskflow.notification;

import com.projectManagement.taskflow.tenant.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @RabbitListener(queues = "notification.queue")
    public void handleEvent(NotificationEvent event) {

        if (event.tenantSchema() == null) {
            throw new IllegalStateException("Tenant missing in event");
        }

        try {
            TenantContext.setTenant(event.tenantSchema());

            log.info("Received event: {}", event);

            messagingTemplate.convertAndSend(
                    "/topic/activity",
//                            + event.eventType().getRoutingKey(),
                    event
            );
        } finally {
            TenantContext.clear();
        }
    }
}
