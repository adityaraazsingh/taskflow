package com.projectManagement.taskflow.notification;

import com.projectManagement.taskflow.entity.ActivityEntity;
import com.projectManagement.taskflow.service.ActivityService;
import com.projectManagement.taskflow.tenant.TenantContext;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//@Transactional
@Service
public class NotificationPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ActivityService activityService;

    public NotificationPublisher(RabbitTemplate rabbitTemplate, ActivityService activityService) {
        this.rabbitTemplate = rabbitTemplate;
        this.activityService = activityService;
    }

    public void publishMemberChange(MemberChangeData data) {
        NotificationEventEnum eventName = NotificationEventEnum.MEMBER_CHANGED;
        String tenantName = TenantContext.getTenant();
        String message = "New member added " + data.memberName() + " to project " + data.projectTitle();
        NotificationEvent event = new NotificationEvent(
                eventName,
                tenantName,
                message
        );

        ActivityEntity activity = new ActivityEntity();
        activity.setEventName(eventName);
        activity.setProjectId(data.projectId());
        activity.setData(message);

        activityService.postActivity(activity);
        rabbitTemplate.convertAndSend("member.exchange", "member.assigned", event);
    }

}
