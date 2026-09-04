package com.projectManagement.taskflow.mapper;

import com.projectManagement.taskflow.entity.ActivityEntity;
import com.projectManagement.taskflow.notification.NotificationEvent;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ActivityMapper {
    public NotificationEvent toNotificationEvent(ActivityEntity activity){
        Map<String, Object> map = new HashMap<>();
        map.put("projectId",activity.getProjectId());
        map.put("taskId",activity.getTaskId());
        NotificationEvent event = new NotificationEvent(activity.getEventName(), activity.getTenantName(), activity.getData(),map,activity.getCreatedAt());
        return event;
    }
}
