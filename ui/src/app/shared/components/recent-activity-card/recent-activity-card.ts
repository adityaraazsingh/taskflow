import { Component,signal, inject, Inject, input } from '@angular/core';
import { NotificationService } from '../../../core/services/notification.service';
import { ActivityService } from '../../../core/services/activity.service';
import { NotificationModel } from '../../../core/models/NotificationModel';
import { Router } from '@angular/router';
import { environment } from "../../../environment";
import { Observable } from 'rxjs/internal/Observable';
import { AsyncPipe } from '@angular/common';
import { map } from 'rxjs';


@Component({
  selector: 'app-recent-activity-card',
  imports: [AsyncPipe],
  templateUrl: './recent-activity-card.html',
  styleUrl: './recent-activity-card.css',
})
export class RecentActivityCard {
  notifications$ = new Observable<NotificationModel[]>();
  
  url = environment.frontEndUrl;
  
  constructor(
    private notificationService: NotificationService, 
    private router : Router
  ) {
    this.notifications$ = this.notificationService.notificationsObs$.pipe(
      map( notifications => notifications.filter(n => {
          const createdAt = new Date(n.createdAt!).getTime();
          const now = Date.now();
          const diffHours = (now - createdAt) / (1000 * 60 * 60);
          return diffHours <= 24;
        }))
    );
  }

  ngOnInit() {
    this.notificationService.loadNotifications();
    this.notificationService.connect();
  }

  navigateToTheEvent(notification: NotificationModel) {
    this.router.navigate([`/projects/${notification.data?.['projectId']}`]);
  }
}     