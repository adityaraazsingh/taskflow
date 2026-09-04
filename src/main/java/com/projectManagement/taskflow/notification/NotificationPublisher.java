package com.projectManagement.taskflow.notification;

import com.projectManagement.taskflow.entity.ActivityEntity;
import com.projectManagement.taskflow.service.ActivityService;
import com.projectManagement.taskflow.tenant.TenantContext;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

//@Transactional
@Service
public class NotificationPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ActivityService activityService;

    public NotificationPublisher(RabbitTemplate rabbitTemplate, ActivityService activityService) {
        this.rabbitTemplate = rabbitTemplate;
        this.activityService = activityService;
    }

    public void publishNotification(String message, Long projectId, Map<String, Object> map, NotificationEventEnum eventName) {
        String tenantName = TenantContext.getTenant();
        NotificationEvent event = new NotificationEvent(
                eventName,
                tenantName,
                message,
                map,
                new Date()
        );

        ActivityEntity activity = new ActivityEntity();
        activity.setEventName(eventName);
        activity.setProjectId(projectId);
        activity.setData(message);

        activityService.postActivity(activity);
        rabbitTemplate.convertAndSend("member.exchange", "member.assigned", event);
    }

}
