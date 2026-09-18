import { Component, input, output } from '@angular/core';
import { NotificationModel } from '../../../core/models/NotificationModel';
import { NotificationEventEnum } from '../../../core/enums/NotificationEventEnum';

@Component({
  selector: 'app-notfication-message',
  imports: [],
  templateUrl: './notfication-message.html',
  styleUrl: './notfication-message.css',
})
export class NotficationMessage {
  notification = input.required<NotificationModel>();
  NotificationEventEnum = NotificationEventEnum;

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
}
