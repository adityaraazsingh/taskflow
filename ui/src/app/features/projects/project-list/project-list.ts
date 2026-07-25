import { Component, OnInit, signal, computed } from '@angular/core';
import { NgClass } from '@angular/common';
import { DatePipe } from '@angular/common';
import { Router } from '@angular/router';
import { ProjectModel } from '../../../core/models/project.model';
import { ProjectService } from '../../../core/services/project.service';
import { ProjectForm } from "../project-form/project-form";
import { Pagination } from "../../../shared/components/pagination/pagination";
import { Status } from '../../../core/enums/Status';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-project-list',
  imports: [NgClass, DatePipe, ProjectForm, Pagination, FormsModule],
  templateUrl: './project-list.html',
  styleUrl: './project-list.css',
})
export class ProjectList implements OnInit {
  allProjects = signal<ProjectModel[]>([]);
  addingProject = signal<boolean>(false);
  Status = Status;
  
  searchQuery = signal<string>('');
  statusFilter : Status| null =(null);
  sortBy = signal<string>('name');
  sortOrder = signal<'asc' | 'desc'>('asc');

  currentPage = signal<number>(0);
  pageSize = signal<number>(9);
  totalPages = signal<number>(1);
  totalItems = signal<number>(0);

  filteredProjects = computed(() => {
    let projects = [...this.allProjects()];

    const query = this.searchQuery().toLowerCase().trim();
    if (query) {
      projects = projects.filter(p =>
        p.name.toLowerCase().includes(query) ||
        (p.description && p.description.toLowerCase().includes(query))
      );
    }

    // Apply sorting
    const sortField = this.sortBy();
    const order = this.sortOrder() === 'asc' ? 1 : -1;

    projects.sort((a, b) => {
      let comparison = 0;
      switch (sortField) {
        case 'name':
          comparison = a.name.localeCompare(b.name);
          break;
        case 'createdAt':
          const dateA = a.createdAt ? new Date(a.createdAt).getTime() : 0;
          const dateB = b.createdAt ? new Date(b.createdAt).getTime() : 0;
          comparison = dateA - dateB;
          break;
        case 'status':
          comparison = a.status.localeCompare(b.status);
          break;
        default:
          comparison = 0;
      }
      return comparison * order;
    });

    return projects;
  });

  constructor(
    private router: Router,
    private projectService: ProjectService
  ) {
    // Check if projects were passed via navigation state
    const navigation = this.router.getCurrentNavigation();
    const state = navigation?.extras.state as { ProjectModel: ProjectModel[] } | null;
    if (state?.ProjectModel) {
      this.allProjects.set(state.ProjectModel);
    }
  }

  ngOnInit(): void {
    // Fetch projects if not already loaded from navigation state
    if (this.allProjects().length === 0) {
      this.fetchProjects();
    }
  }

  fetchProjects(): void {
    this.projectService.getProjectsForCurrentUser(this.currentPage(), this.pageSize(),this.sortOrder(), this.statusFilter).subscribe({
      next: (response) => {
        this.allProjects.set(response.content);
        this.totalPages.set(response.totalPages);
        this.totalItems.set(response.totalElements);
      },
      error: (err) => {
        console.error('Failed to fetch projects:', err);
      }
    });
  }

  onClickCreateProject(): void {
    this.addingProject.set(!this.addingProject());
  }

  toggleSortOrder(): void {
    this.sortOrder.set(this.sortOrder() === 'asc' ? 'desc' : 'asc');
    this.fetchProjects()
  }

  clearFilters(): void {
    this.searchQuery.set('');
    this.statusFilter=null;
    this.sortBy.set('name');
    this.sortOrder.set('asc');
    this.fetchProjects();
  }

  onPageChange(page: number): void {
    this.currentPage.set(page);
    this.fetchProjects();
  }

  navigateToProject(projectId: number): void {
    this.router.navigate([`/projects/${projectId}`]);
  }
}
