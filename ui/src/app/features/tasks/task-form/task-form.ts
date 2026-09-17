import { Component, signal, output, input, OnInit } from '@angular/core';
import { TaskService } from '../../../core/services/task.service';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TaskModel } from '../../../core/models/task.model';
import { ProjectService } from '../../../core/services/project.service';
import { Status } from '../../../core/enums/Status';
import { Priority } from '../../../core/enums/Priority';

@Component({
  selector: 'app-task-form',
  imports: [ReactiveFormsModule],
  templateUrl: './task-form.html',
  styleUrl: './task-form.css',
})
export class TaskForm implements OnInit{
  isEdit = false;
  closeDialog = output();
  task = input<TaskModel>();
  projectId = input<number>();
  submitting = signal(false);

  taskForm = new FormGroup({
    title: new FormControl('', Validators.required),
    description: new FormControl('', Validators.required),
    status: new FormControl<Status>(Status.TODO, Validators.required),
    priority: new FormControl<Priority>(Priority.LOW, Validators.required),
    // assigneeId: new FormControl('', Validators.required),
    duedate: new FormControl<Date>(new Date(), Validators.required),
  });

  constructor(private taskService: TaskService, private projectService: ProjectService) { }

  ngOnInit(){
    this.taskForm.patchValue({
      title : this.task()?.title,
      description : this.task()?.description,
      status : this.task()?.status,
      priority : this.task()?.priority,
      duedate: this.task()?.dueDate
    });
  }

  closingAddingTaskDialog($event: any) {
    this.closeDialog.emit();
  }

  submitTaskForm() {
    if(this.taskForm.invalid) return;

    this.submitting.set(true);

    if(this.task()){
      const payload: TaskModel = {
        title: this.taskForm.get('title')!.value ?? '',
        description: this.taskForm.get('description')!.value ?? '',
        status: this.taskForm.get('status')!.value ?? Status.TODO,
        priority: this.taskForm.get('priority')!.value ?? Priority.LOW,
        dueDate: this.taskForm.get('duedate')!.value ?? new Date(),
      }
      this.task()!.title = payload.title;
      this.task()!.description = payload.description;
      this.task()!.status = payload.status;
      this.task()!.priority = payload.priority;
      this.task()!.dueDate = payload.dueDate;

      this.taskService.updateTaskById(this.task()!.id! , this.task()!).subscribe({
        next: () => {
          this.submitting.set(false);
          this.closeDialog.emit();
        },
        error: (err) => {
          console.error(err);
          this.submitting.set(false);
        }
      });
    } else {
      const payload: TaskModel = {
        title: this.taskForm.get('title')!.value ?? '',
        description: this.taskForm.get('description')!.value ?? '',
        status: this.taskForm.get('status')!.value ?? Status.TODO,
        priority: this.taskForm.get('priority')!.value ?? Priority.LOW,
        dueDate: this.taskForm.get('duedate')!.value ?? new Date(),
      }
      // Create new task
      this.taskService.createTask(this.projectId()!, payload).subscribe({
        next: () => {
          this.submitting.set(false);
          this.closeDialog.emit();
        },
        error: (err) => {
          console.error(err);
          this.submitting.set(false);
        }
      });
    }
  }
}
