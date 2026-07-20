import { Component, Input } from '@angular/core';
import { NgClass, TitleCasePipe } from '@angular/common';

@Component({
  selector: 'app-status-badge',
  imports: [NgClass, TitleCasePipe],
  templateUrl: './status-badge.html',
  styleUrl: './status-badge.css',
})
export class StatusBadge {
  @Input() status: 'TODO' | 'IN_PROGRESS' | 'DONE' | 'ACTIVE' | 'COMPLETED' | 'ARCHIVED' = 'TODO';
}
