import { Component, input, output, computed, inject } from '@angular/core';
import { TaskModel } from '../../../core/models/task.model';
import { TaskCard } from "../../../shared/components/task-card/task-card";
import { Status } from '../../../core/enums/Status';
import { Router } from '@angular/router';

@Component({
  selector: 'app-task-board',
  imports: [TaskCard],
  templateUrl: './task-board.html',
  styleUrl: './task-board.css',
})
export class TaskBoard {
  tasks = input.required<TaskModel[]>();
  taskClick = output<TaskModel>();
  Status = Status;
  router = inject(Router);

  todoTasks = computed(() => this.tasks().filter(t => t.status === Status.TODO));
  inProgressTasks = computed(() => this.tasks().filter(t => t.status === Status.IN_PROGRESS));
  doneTasks = computed(() => this.tasks().filter(t => t.status === Status.DONE));

  onTaskClick(task: TaskModel) {
    this.router.navigate([`tasks/${task.id}`],
      { state: { 
        task
      } });
    // this.taskClick.emit(task);
  }
}
