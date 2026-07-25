import { Component, OnInit, signal } from '@angular/core';
import { NgClass } from '@angular/common';
import { DatePipe } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
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

  constructor(private router: Router, private projectService : ProjectService, private route : ActivatedRoute) {
    const navigation = this.router.getCurrentNavigation();
        const state = navigation?.extras.state as { ProjectModel: ProjectModel[] };
        if (state?.ProjectModel) {
          this.projects.set(state?.ProjectModel);
          console.log(this.projects())
        }
  }

  ngOnInit(): void {}

  onClickCreateProject(){
    this.addingProject.set(!this.addingProject());
  }

  navigateToProject(projectId : number){
    this.router.navigate([`/projects/${projectId}`])
  }

}
