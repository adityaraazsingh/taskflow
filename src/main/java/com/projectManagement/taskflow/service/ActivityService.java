package com.projectManagement.taskflow.service;

import com.projectManagement.taskflow.entity.ActivityEntity;
import com.projectManagement.taskflow.repository.ActivityRepo;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ActivityService {

    private final ActivityRepo activityRepo;

    public ActivityService(ActivityRepo activityRepo) {
        this.activityRepo = activityRepo;
    }

    public List<ActivityEntity> getAllActivities(){
        return activityRepo.findAll();
    }

    public ActivityEntity getActivity(Long activityId){
        return activityRepo.getById(activityId);
    }

    public ActivityEntity postActivity(ActivityEntity activity){
        return activityRepo.save(activity);
    }
}
