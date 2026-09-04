import { Component, EventEmitter, Inject, inject, OnInit, output, Output } from '@angular/core';
import { Observable } from 'rxjs';
import { NotificationModel } from '../../../core/models/NotificationModel';
import { NotificationService } from '../../../core/services/notification.service';
import { AsyncPipe } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-notification-dialog',
  imports: [AsyncPipe],
  templateUrl: './notification-dialog.html',
  styleUrl: './notification-dialog.css',
})
export class NotificationDialog {
  @Output() closed = new EventEmitter<void>();
  newNotificationCount = output<number>();

  notifications$ = new Observable<NotificationModel[]>();
  router = inject(Router);
  notificationService = inject(NotificationService);


  ngOnInit() {
    this.notifications$ = this.notificationService.notificationsObs$;
    this.notificationService.loadNotifications();
    this.notificationService.connect();
  }

  navigateToTheEvent(notification: NotificationModel) {
    console.log("Notification Event")
    this.router.navigate([`/projects/${notification.data?.['projectId']}`]);
    this.closeDialog()
  }

  closeDialog() {
    this.closed.emit();
  }
}