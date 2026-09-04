package com.projectManagement.taskflow.controller;

import com.projectManagement.taskflow.mapper.ActivityMapper;
import com.projectManagement.taskflow.notification.NotificationEvent;
import com.projectManagement.taskflow.service.ActivityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/activity")
@RestController
public class ActivityController {

    private final ActivityService activityService;
    private final ActivityMapper activityMapper;

    public ActivityController(ActivityService activityService, ActivityMapper activityMapper) {
        this.activityService = activityService;
        this.activityMapper = activityMapper;
    }

    @GetMapping
    public List<NotificationEvent> getActivities(){
        return activityService.getAllActivities().stream().map(activityMapper::toNotificationEvent).toList();
    }
}
