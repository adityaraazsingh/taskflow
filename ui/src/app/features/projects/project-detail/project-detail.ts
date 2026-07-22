import { Component, inject, signal, computed } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { TaskBoard } from '../../tasks/task-board/task-board';
import { ProjectService } from '../../../core/services/project.service';
import { ProjectModel } from '../../../core/models/project.model';
import { TaskModel } from '../../../core/models/task.model';
import { TaskForm } from "../../tasks/task-form/task-form";
import { StatusBadge } from '../../../shared/components/status-badge/status-badge';
import { LoadingSpinner } from '../../../shared/components/loading-spinner/loading-spinner';
import { Status } from '../../../core/enums/Status';
import { DatePipe } from '@angular/common';
import { ProjectForm } from "../project-form/project-form";
import { ConfirmDialog } from "../../../shared/components/confirm-dialog/confirm-dialog";

@Component({
  selector: 'app-project-detail',
  imports: [RouterLink, TaskBoard, TaskForm, StatusBadge, LoadingSpinner, DatePipe, ProjectForm, ConfirmDialog],
  templateUrl: './project-detail.html',
  styleUrl: './project-detail.css',
})
export class ProjectDetail {
  projectId!: number;
  editingProject : boolean = false;
  deletingProject : boolean = false;

  project = signal<ProjectModel | null>(null);
  tasks = signal<TaskModel[]>([]);
  loading = signal(true);

  addingTask = false;
  taskToBeEdited!: TaskModel;

  // Computed values
  completedTasks = computed(() => this.tasks().filter(t => t.status === Status.DONE).length);
  totalTasks = computed(() => this.tasks().length);
  progressPercent = computed(() => {
    const total = this.totalTasks();
    if (total === 0) return 0;
    return Math.round((this.completedTasks() / total) * 100);
  });

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private projectService: ProjectService
  ) {
    this.projectId = +this.route.snapshot.paramMap.get('id')!;

    // Fetch project details
    this.projectService.getOneProject(this.projectId).subscribe({
      next: (project) => {
        this.project.set(project);
      },
      error: (err) => {
        console.error('Error fetching project', err);
      }
    });

    // Fetch project tasks
    this.projectService.getAllTaskOfProject(this.projectId).subscribe({
      next: (page) => {
        this.tasks.set(page.content);
        this.loading.set(false);
      },
      error: (err) => {
        window.alert(`Error fetching project details: ${err}`);
        this.loading.set(false);
      }
    });
  }

  onClickingMember() {
    this.router.navigate([`/projects/${this.projectId}/members`]);
  }

  toggleTheDialog(task: TaskModel | null = null) {
    if (task) {
      this.taskToBeEdited = task;
    } else {
      // When creating a new task, clear the edit target
      this.taskToBeEdited = null as any;
    }
    this.addingTask = !this.addingTask;
  }

  onEditingProject(){
    this.editingProject = !this.editingProject;
  }

  onTogglingDelete(){
    this.deletingProject = !this.deletingProject;
  }

  deleteProject(){
    this.projectService.deleteProject(this.project()?.id!).subscribe(
      (next)=>{
        window.alert("Project Deleted");
      }
    )
    this.onTogglingDelete();
  }

}
