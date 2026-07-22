import { Component, inject, signal } from '@angular/core';
import { ProjectCard } from '../../shared/components/project-card/project-card';
import { RecentActivityCard } from '../../shared/components/recent-activity-card/recent-activity-card';
import { ProjectService } from '../../core/services/project.service';
import { ProjectModel } from '../../core/models/project.model';
import { Router } from '@angular/router';
import { ProjectForm } from "../projects/project-form/project-form";

@Component({
  selector: 'app-dashboard',
  imports: [ProjectCard, RecentActivityCard, ProjectForm],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard {
  projects = signal<ProjectModel[]>([]) ;
  addingProject : boolean = false;
  router = inject(Router);
  constructor(private projectService : ProjectService){
    this.projectService.getAllProject().subscribe(
      next => { this.projects.set(next) }
    )
  }

  navigateToProject(projectId : number){
    this.router.navigate([`/projects/${projectId}`]);
  }
  
  onClickingProfile(){
    this.router.navigate([`/profile`]);
  }

  onAddingProject(){
    this.addingProject = !this.addingProject;
  }

  clickOnViewAllProject(){
    this.router.navigate([`/allprojects`],{state :{
      ProjectModel : this.projects()
    }});
  }
}
