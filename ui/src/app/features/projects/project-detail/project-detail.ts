import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { TaskBoard } from '../../tasks/task-board/task-board';

@Component({
  selector: 'app-project-detail',
  imports: [RouterLink, TaskBoard],
  templateUrl: './project-detail.html',
  styleUrl: './project-detail.css',
})
export class ProjectDetail {}
