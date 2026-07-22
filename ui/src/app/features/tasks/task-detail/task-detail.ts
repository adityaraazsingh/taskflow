import { Component } from '@angular/core';
import { CommentList } from '../comment-list/comment-list';
import { Router } from '@angular/router';
import { TaskModel } from '../../../core/models/task.model';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-task-detail',
  imports: [CommentList, DatePipe],
  templateUrl: './task-detail.html',
  styleUrl: './task-detail.css',
})
export class TaskDetail {

  task! : TaskModel;

  constructor(private router: Router) {
    const navigation = this.router.getCurrentNavigation();
    const state = navigation?.extras.state as { task: TaskModel };
      if (state?.task) {
        this.task = state.task;
      }
  }

}
