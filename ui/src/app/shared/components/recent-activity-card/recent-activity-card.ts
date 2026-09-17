import { Component, signal, inject, Inject, input } from '@angular/core';
import { NotificationService } from '../../../core/services/notification.service';
import { ActivityService } from '../../../core/services/activity.service';
import { NotificationModel } from '../../../core/models/NotificationModel';
import { Router } from '@angular/router';
import { Observable } from 'rxjs/internal/Observable';
import { AsyncPipe } from '@angular/common';
import { map } from 'rxjs';
import { NotficationMessage } from "../notfication-message/notfication-message";


@Component({
  selector: 'app-recent-activity-card',
  imports: [AsyncPipe, NotficationMessage],
  templateUrl: './recent-activity-card.html',
  styleUrl: './recent-activity-card.css',
})
export class RecentActivityCard {
  notifications$ = new Observable<NotificationModel[]>();
  loadError$: Observable<boolean>;

  constructor(
    private notificationService: NotificationService, 
    private router : Router
  ) {
    this.loadError$ = this.notificationService.loadErrorObs$;
    this.notifications$ = this.notificationService.notificationsObs$.pipe(
      map(notifications => {
        const list = Array.isArray(notifications) ? notifications : [];
        const recent = list.filter(n => {
          if (!n?.createdAt) return true; // keep undated entries instead of dropping them
          const createdAt = new Date(n.createdAt).getTime();
          if (Number.isNaN(createdAt)) return true;
          const diffHours = (Date.now() - createdAt) / (1000 * 60 * 60);
          return diffHours <= 24;
        });
        // Newest first
        return recent.slice().sort((a, b) => {
          const ta = a?.createdAt ? new Date(a.createdAt).getTime() : 0;
          const tb = b?.createdAt ? new Date(b.createdAt).getTime() : 0;
          return tb - ta;
        });
      })
    );
  }

  ngOnInit() {
    this.notificationService.loadNotifications();
  }

  retryLoad() {
    this.notificationService.loadNotifications();
  }

  getTimeAgo(dateString?: Date): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffMins = Math.floor(diffMs / (1000 * 60));
    const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
    const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));

    if (diffMins < 1) return 'Just now';
    if (diffMins < 60) return `${diffMins}m ago`;
    if (diffHours < 24) return `${diffHours}h ago`;
    if (diffDays < 7) return `${diffDays}d ago`;
    return date.toLocaleDateString();
  }

  navigateToTheEvent(notification: NotificationModel) {
    this.router.navigate([`/projects/${notification.data?.['projectId']}`]);
  }
}