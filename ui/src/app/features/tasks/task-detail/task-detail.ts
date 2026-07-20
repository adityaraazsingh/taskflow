import { Component } from '@angular/core';
import { CommentList } from '../comment-list/comment-list';

@Component({
  selector: 'app-task-detail',
  imports: [CommentList],
  templateUrl: './task-detail.html',
  styleUrl: './task-detail.css',
})
export class TaskDetail {}
