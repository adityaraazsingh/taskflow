import { Component, Input } from '@angular/core';
import { NgClass } from '@angular/common';

@Component({
  selector: 'app-priority-tag',
  imports: [NgClass],
  templateUrl: './priority-tag.html',
  styleUrl: './priority-tag.css',
})
export class PriorityTag {
  @Input() priority: 'LOW' | 'MED' | 'HIGH' = 'MED';
}
