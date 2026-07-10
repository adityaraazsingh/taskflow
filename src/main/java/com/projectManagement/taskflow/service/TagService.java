package com.projectManagement.taskflow.service;

import com.projectManagement.taskflow.dto.TagRequestDTO;
import com.projectManagement.taskflow.entity.TagEntity;
import com.projectManagement.taskflow.entity.TaskEntity;
import com.projectManagement.taskflow.repository.TagRepo;
import com.projectManagement.taskflow.repository.TaskRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TagService {
    @Autowired
    private TagRepo tagRepo;

    @Autowired
    private TaskRepo taskRepo;

    public TagEntity createTag(TagRequestDTO tagRequest){
        TagEntity tag = new TagEntity();
        tag.setName(tagRequest.name);
        tag.setColorHex(tagRequest.colorHex);

        return tagRepo.save(tag);
    }

//    TODO: create exceptions for 'tag not found too'
    public String AttachTagToTask(Long taskId, Long tagId){
        TagEntity tag = tagRepo.findById(tagId)
                .orElseThrow(()-> new RuntimeException("Tag not found"));

        TaskEntity task = taskRepo.findById(taskId)
                .orElseThrow(()-> new RuntimeException("Task not found"));

        if (!task.getTags().contains(tag)) {
            task.getTags().add(tag);
            tag.getTasks().add(task); // optional but good practice
        }
//        TODO : remove tagRepo.save as owning side is TaskRepo you can see it
        tagRepo.save(tag);
        taskRepo.save(task);
        return " Tag added to the task with id "+taskId;
    }

    public String removeTagFromTask(Long taskId, Long tagId){
        TagEntity tag = tagRepo.findById(tagId)
                .orElseThrow(()-> new RuntimeException("Tag not found"));

        TaskEntity task = taskRepo.findById(taskId)
                .orElseThrow(()-> new RuntimeException("Task not found"));

        if (task.getTags().contains(tag)) {
            task.getTags().remove(tag);
            tag.getTasks().remove(task); // optional but good practice
        }

        tagRepo.save(tag);
        taskRepo.save(task);

        return "Successfully removed the Tag";
    }
}
