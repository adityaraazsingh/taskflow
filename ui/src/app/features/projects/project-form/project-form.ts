import { Component, signal, output, input, OnInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ProjectService } from '../../../core/services/project.service';
import { ProjectModel } from '../../../core/models/project.model';
import { Status } from '../../../core/enums/Status';

@Component({
  selector: 'app-project-form',
  imports: [ReactiveFormsModule],
  templateUrl: './project-form.html',
  styleUrl: './project-form.css',
})
export class ProjectForm implements OnInit {
  closeDialog = output();
  project = input<ProjectModel>();

  selectedColor = signal('indigo');
  submitting = signal(false);

  colors = [
    { value: 'indigo', class: 'bg-indigo-500', ring: 'ring-indigo-500' },
    { value: 'emerald', class: 'bg-emerald-500', ring: 'ring-emerald-500' },
    { value: 'amber', class: 'bg-amber-500', ring: 'ring-amber-500' },
    { value: 'rose', class: 'bg-rose-500', ring: 'ring-rose-500' },
    { value: 'purple', class: 'bg-purple-500', ring: 'ring-purple-500' },
    { value: 'sky', class: 'bg-sky-500', ring: 'ring-sky-500' },
  ];

  projectForm = new FormGroup({
    name: new FormControl('', Validators.required),
    description: new FormControl('', Validators.required),
    status: new FormControl('TODO', Validators.required),
  });

  constructor(private projectService: ProjectService) {}

  ngOnInit() {
    if (this.project()) {
      this.projectForm.patchValue({
        name: this.project()?.name,
        description: this.project()?.description,
        status: this.project()?.status,
      });
    }
  }

  get isEdit(): boolean {
    return !!this.project();
  }

  closingDialog($event: any) {
    this.closeDialog.emit($event);
  }

  submitProjectForm() {
    if (this.projectForm.invalid) return;

    this.submitting.set(true);

    const payload: ProjectModel = {
      name: this.projectForm.get('name')!.value ?? '',
      description: this.projectForm.get('description')!.value ?? '',
      status: (this.projectForm.get('status')!.value ?? 'TODO') as Status,
      // userId: this.project()?.userId ?? 0,
      taskIds: this.project()?.taskIds ?? [],
      projectMemberIds: this.project()?.projectMemberIds ?? [],
    };

    if (this.isEdit && this.project()?.id) {
      this.projectService.updateProject(this.project()!.id!, payload).subscribe({
        next: () => {
          this.submitting.set(false);
          this.closeDialog.emit();
        },
        error: (err) => {
          console.error('Error updating project', err);
          this.submitting.set(false);
        },
      });
    } else {
      this.projectService.createProject(payload).subscribe({
        next: () => {
          this.submitting.set(false);
          this.closeDialog.emit();
        },
        error: (err) => {
          console.error('Error creating project', err);
          this.submitting.set(false);
        },
      });
    }
  }
}
