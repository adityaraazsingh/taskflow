package com.projectManagement.taskflow.service;

import com.projectManagement.taskflow.dto.TagRequestDTO;
import com.projectManagement.taskflow.dto.TagResponseDto;
import com.projectManagement.taskflow.entity.TagEntity;
import com.projectManagement.taskflow.entity.TaskEntity;
import com.projectManagement.taskflow.exception.TaskNotFoundException;
import com.projectManagement.taskflow.mapper.TagMapper;
import com.projectManagement.taskflow.repository.TagRepo;
import com.projectManagement.taskflow.repository.TaskRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TagService {

    private final TagRepo tagRepo;
    private final TaskRepo taskRepo;
    private final TagMapper tagMapper;

    public TagService(TagRepo tagRepo, TaskRepo taskRepo, TagMapper tagMapper) {
        this.tagRepo = tagRepo;
        this.taskRepo = taskRepo;
        this.tagMapper = tagMapper;
    }

    public TagResponseDto createTag(TagRequestDTO tagRequest){
        TagEntity tag = tagMapper.toEntity(tagRequest);
        return tagMapper.toDto(tagRepo.save(tag));
    }

    public List<TagResponseDto> getAllTags(){
        return tagRepo.findAll().stream().map(tagMapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TagResponseDto getTagById(Long id){
        TagEntity tag = tagRepo.findById(id).orElseThrow(()->new RuntimeException("Tag Not Found"));
        return tagMapper.toDto(tag);
    }

//    TODO: create exceptions for 'tag not found too'
    public String AttachTagToTask(Long taskId, Long tagId){
        TagEntity tag = tagRepo.findById(tagId)
                .orElseThrow(()-> new RuntimeException("Tag not found"));

        TaskEntity task = taskRepo.findById(taskId)
                .orElseThrow(()-> new TaskNotFoundException("Task not found"));

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

    public List<TagResponseDto> getTagsByTaskId(Long taskId) {
        TaskEntity task = taskRepo.findById(taskId).orElseThrow(()-> new TaskNotFoundException("Task Not Found"));
        List<Long> tagIds = task.getTags().stream().map(TagEntity::getId).collect(Collectors.toList());

        return tagIds.stream().map((id)-> tagMapper.toDto(tagRepo.findById(id).orElseThrow(
                ()-> new RuntimeException("Tag not found")
        ))).collect(Collectors.toList());
    }

    public List<TagResponseDto> postTags(List<TagRequestDTO> tags){
        return tagRepo.saveAll(
                        tags.stream().map(tagMapper::toEntity)
                                .collect(Collectors.toList()))
                .stream()
                .map(tagMapper::toDto)
                .collect(Collectors.toList());
    }
}
