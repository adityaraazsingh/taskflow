import { Component, OnInit, signal } from '@angular/core';
import { NgClass } from '@angular/common';
import { DatePipe } from '@angular/common';
import { Router } from '@angular/router';
import { ProjectModel } from '../../../core/models/project.model';
import { ProjectService } from '../../../core/services/project.service';
import { ProjectForm } from "../project-form/project-form";

@Component({
  selector: 'app-project-list',
  imports: [NgClass, DatePipe, ProjectForm],
  templateUrl: './project-list.html',
  styleUrl: './project-list.css',
})
export class ProjectList implements OnInit{
  addingProject = signal<boolean>(false);
  projects= signal<ProjectModel[]>([]);

  constructor(private router: Router, private projectService : ProjectService) {}

  ngOnInit(): void {
     this.projectService.getAllProject().subscribe(
      (next)=>{
        this.projects.set(next);
      }
    )
  }

  onClickCreateProject(){
    this.addingProject.set(!this.addingProject());
  }

  navigateToProject(projectId : number){
    this.router.navigate([`/projects/${projectId}`])
  }

}
