import { Component, Input } from '@angular/core';
import { NgClass } from '@angular/common';
import { Priority } from '../../../core/enums/Priority';

@Component({
  selector: 'app-priority-tag',
  imports: [NgClass],
  templateUrl: './priority-tag.html',
  styleUrl: './priority-tag.css',
})
export class PriorityTag {
  @Input() priority:Priority = Priority.MEDIUM;
}
