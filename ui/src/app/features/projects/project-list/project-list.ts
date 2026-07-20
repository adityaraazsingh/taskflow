import { Component } from '@angular/core';
import { NgClass } from '@angular/common';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-project-list',
  imports: [NgClass, DatePipe],
  templateUrl: './project-list.html',
  styleUrl: './project-list.css',
})
export class ProjectList {
  projects: any[] = [];
}
