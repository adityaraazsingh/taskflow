import { Component, inject, Inject, input } from '@angular/core';
import { NotificationService } from '../../../core/services/notification.service';
import { ActivityService } from '../../../core/services/activity.service';
import { NotificationModel } from '../../../core/models/NotificationModel';

@Component({
  selector: 'app-recent-activity-card',
  imports: [],
  templateUrl: './recent-activity-card.html',
  styleUrl: './recent-activity-card.css',
})
export class RecentActivityCard {
  // recentActivityStatus = input.required<string>();
  notificationService = inject(NotificationService);
  activityService = inject(ActivityService);
  notifications: NotificationModel[] = [];

  ngOnInit() {
    this.activityService.getActivity().subscribe((activities: NotificationModel[]) => {
      this.notifications = activities;
      console.log('Recent activities:', activities);
    });
  }
}

      