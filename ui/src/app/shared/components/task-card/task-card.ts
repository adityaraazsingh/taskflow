import { Component, input, output } from '@angular/core';
import { NgClass } from '@angular/common';
import { TaskModel } from '../../../core/models/task.model';
import { StatusBadge } from '../status-badge/status-badge';
import { PriorityTag } from '../priority-tag/priority-tag';
import { Status } from '../../../core/enums/Status';
import { Priority } from '../../../core/enums/Priority';

@Component({
  selector: 'app-task-card',
  imports: [NgClass, StatusBadge, PriorityTag],
  templateUrl: './task-card.html',
  styleUrl: './task-card.css',
})
export class TaskCard {
  task = input.required<TaskModel>();
  onClick = output<TaskModel>();

  get priorityBorderClass(): string {
    const p: any = this.task().priority;
    if (p === 'HIGH' || p === Priority.HIGH || p === 'MEDIUM') return 'border-l-rose-400';
    if (p === 'MED' || p === Priority.MEDIUM) return 'border-l-amber-400';
    return 'border-l-sky-400';
  }

  get priorityLabel(): 'LOW' | 'MED' | 'HIGH' {
    const p: any = this.task().priority;
    if (p === 'HIGH' || p === Priority.HIGH) return 'HIGH';
    if (p === 'MED' || p === Priority.MEDIUM) return 'MED';
    return 'LOW';
  }

  getDueDateLabel(dueDate?: Date): string {
    if (!dueDate) return '';
    if (this.task().status === Status.DONE) return 'Completed';
    const now = new Date();
    const due = new Date(dueDate);
    const diffMs = due.getTime() - now.getTime();
    const diffDays = Math.ceil(diffMs / (1000 * 60 * 60 * 24));

    if (diffDays < 0) return `${Math.abs(diffDays)}d overdue`;
    if (diffDays === 0) return 'Today';
    if (diffDays === 1) return 'Tomorrow';
    if (diffDays <= 7) return `${diffDays}d`;
    if (diffDays <= 30) return `${Math.floor(diffDays / 7)}w`;
    return `${Math.floor(diffDays / 30)}mo`;
  }

  isOverdue(dueDate?: Date): boolean {
    if (!dueDate) return false;
    if (this.task().status === Status.DONE) return false;
    return new Date(dueDate).getTime() < new Date().getTime();
  }

  get assigneeInitial(): string {
    const id = this.task().assigneeId;
    return id != null ? id.toString().slice(-2) : '?';
  }

  onTaskClick(): void {
    this.onClick.emit(this.task());
  }
}
