import { Component } from '@angular/core';
import { NgClass } from '@angular/common';

@Component({
  selector: 'app-project-members',
  imports: [NgClass],
  templateUrl: './project-members.html',
  styleUrl: './project-members.css',
})
export class ProjectMembers {
  members: any[] = [];
}
