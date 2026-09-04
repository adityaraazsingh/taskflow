import { NotificationEventEnum } from "../enums/NotificationEventEnum";

export interface NotificationModel {
  eventType: NotificationEventEnum;
  message: string;
  tenantSchema: string;
  data?: Record<string, any>;
  createdAt?: Date;
}