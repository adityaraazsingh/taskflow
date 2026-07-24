import { Component, OnInit, signal } from '@angular/core';
import { CommentList } from '../comment-list/comment-list';
import { ActivatedRoute, Router } from '@angular/router';
import { TaskModel } from '../../../core/models/task.model';
import { DatePipe, NgClass } from '@angular/common';
import { TaskService } from '../../../core/services/task.service';
import { CommentModel } from '../../../core/models/comment.model';
import { TagService } from '../../../core/services/tag.service';
import { TagModel } from '../../../core/models/tag.model';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Status } from '../../../core/enums/Status';
import { Priority } from '../../../core/enums/Priority';
import { statusChangeRequestDto } from '../../../core/models/statusChangeRequestDto';
import { priorityChangeRequestDto } from '../../../core/models/priorityChangeRequestDto';
import { TaskForm } from "../task-form/task-form";
import { ConfirmDialog } from "../../../shared/components/confirm-dialog/confirm-dialog";

@Component({
  selector: 'app-task-detail',
  imports: [CommentList, DatePipe, FormsModule, ReactiveFormsModule, TaskForm, ConfirmDialog],
  templateUrl: './task-detail.html',
  styleUrl: './task-detail.css',
})
export class TaskDetail implements OnInit {

  task!: TaskModel;
  Status = Status;
  editingTask = signal<boolean>(false);
  deletingTask = signal<boolean>(false);
  status = signal<Status>(Status.TODO);
  priority = signal<Priority>(Priority.LOW);
  tags = signal<TagModel[]>([]);
  allTags = signal<TagModel[]>([]);
  comments = signal<CommentModel[]>([]);
  addingTag = signal<boolean>(false);

  form = new FormGroup({
    status : new FormControl(),
    priority : new FormControl()
  });

  constructor(private router: Router, private route: ActivatedRoute, private taskService: TaskService, private tagService : TagService) {
    const navigation = this.router.getCurrentNavigation();
    const state = navigation?.extras.state as { task: TaskModel };
    if (state?.task) {
      this.task = state.task;
      this.status.set(this.task.status)
      this.priority.set(this.task.priority)
    }

    
  }

  ngOnInit(): void {
    this.getCommentsForTask();
    this.getTagsOnATask();
    this.getAllTags();  
    console.log("Patching Values");

    this.form.controls.status.patchValue(this.status());
    this.form.controls.priority.patchValue(this.priority()); 
    this.onChangingStatus()   
    this.onChangingPriority()
  }

  onChangingStatus(){
    this.form.controls.status.valueChanges.subscribe((data)=>{
      const payload : statusChangeRequestDto ={
        status : data
      }
      this.taskService.changeStatusOfTask(this.task.id!, payload).subscribe(
        (next)=>{
          console.log(next)
        }
      )
    })
  }

  onChangingPriority(){
    this.form.controls.priority.valueChanges.subscribe((data)=>{
      const payload : priorityChangeRequestDto ={
        priority : data
      }
      this.taskService.changePriorityOfTask(this.task.id!, payload).subscribe(
        (next)=>{
          console.log(next)
        }
      )
    })
  }

  getCommentsForTask(){
     this.taskService.getCommentsForTask(this.task.id!, 0, 20).subscribe(
      (data) => {
        this.comments.set(data.content)
      }
    )
  }

  getTagsOnATask(){
    this.taskService.getTagsOnATask(this.task.id!).subscribe(
      (data)=>{
        this.tags.set(data);
        console.log("GetTask ON a Task is called")
      }
    )
  }

  getAllTags(){
     this.tagService.getTags().subscribe(
      (data)=>{
        this.allTags.set(data);
        console.log(data);
      }
    )
  }

  clickOnAddTag() {
    this.addingTag.set(!this.addingTag());
  }

  addingTagToATask(tagId : number){
    this.taskService.addTasksPerTags(this.task.id!, tagId).subscribe(
      (data)=>{
      }
    );
    this.getTagsOnATask()
  }

  deleteTagOfATask(tagId : number){
    this.taskService.deleteTagForTask(this.task.id!, tagId).subscribe(
      (data)=>{
      }
    );
    this.getTagsOnATask()
  }

  toggleTheDialog(){
    this.editingTask.set(!this.editingTask())
  }

  onClickDelete(){
    this.deletingTask.set(!this.deletingTask())
  }

  deletingTaskWithId(){
    this.taskService.deleteTask(this.task.id!).subscribe(
      (next)=>{
        console.log(next)
      }
    )
    this.onClickDelete()
    this.router.navigate([`projects/${this.task.projectId}`]);
  }

}
