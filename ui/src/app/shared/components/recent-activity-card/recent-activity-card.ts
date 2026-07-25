import { Component, input } from '@angular/core';

@Component({
  selector: 'app-recent-activity-card',
  imports: [],
  templateUrl: './recent-activity-card.html',
  styleUrl: './recent-activity-card.css',
})
export class RecentActivityCard {
  recentActivityStatus = input.required<string>();
}
