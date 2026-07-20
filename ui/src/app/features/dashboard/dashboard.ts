import { Component, signal } from '@angular/core';
import { ProjectCard } from '../../shared/components/project-card/project-card';
import { RecentActivityCard } from '../../shared/components/recent-activity-card/recent-activity-card';
import { ProjectService } from '../../core/services/project.service';
import { ProjectModel } from '../../core/models/project.model';

@Component({
  selector: 'app-dashboard',
  imports: [ProjectCard, RecentActivityCard],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard {
  projects = signal<ProjectModel[]>([]) ;
  constructor(private projectService : ProjectService){
    this.projectService.getAllProject().subscribe(
      next => { this.projects.set(next) , console.log(next) }
    )
  }
}
