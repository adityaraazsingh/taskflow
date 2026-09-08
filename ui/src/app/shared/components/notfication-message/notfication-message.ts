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

}
