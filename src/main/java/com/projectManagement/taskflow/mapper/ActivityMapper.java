package com.projectManagement.taskflow.mapper;

import com.projectManagement.taskflow.entity.ActivityEntity;
import com.projectManagement.taskflow.notification.NotificationEvent;
import org.springframework.stereotype.Component;

@Component
public class ActivityMapper {
    public NotificationEvent toNotificationEvent(ActivityEntity activity){
        NotificationEvent event = new NotificationEvent(activity.getEventName(), activity.getTenantName(), activity.getData());
        return event;
    }
}
