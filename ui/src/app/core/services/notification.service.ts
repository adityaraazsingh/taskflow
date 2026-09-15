import { Injectable, OnInit } from "@angular/core";
import { environment } from "../../environment";
import { Client, StompSubscription } from "@stomp/stompjs";
import { HttpClient } from "@angular/common/http";
import { NotificationModel } from "../models/NotificationModel";
import { BehaviorSubject, Subject } from "rxjs";

@Injectable({
  providedIn: 'root'
})

export class NotificationService implements OnInit {
  private stompClient: Client | null = null;
  private subscription: StompSubscription | undefined;
  
  private url = environment.apiUrl;
  
  private notifications$ = new BehaviorSubject<NotificationModel[]>([]);
  public notificationsObs$ = this.notifications$.asObservable();

  private newNotification$ = new BehaviorSubject<NotificationModel>({} as NotificationModel);
  public newNotificationObs$ = this.newNotification$.asObservable();

  constructor(private httpClient: HttpClient) { }

  ngOnInit() {
    this.loadNotifications();
  }

  loadNotifications() {
    this.httpClient.get<NotificationModel[]>(`${this.url}/activity`)
      .subscribe(data => this.notifications$.next(data));
  }

  connect() {
    if (this.stompClient?.active) return; 
    const token = typeof window !== 'undefined'
      ? localStorage.getItem('accessToken')
      : null;
    this.stompClient = new Client({
      brokerURL: 'ws://localhost:8080/ws', 
      connectHeaders: {
        Authorization: 'Bearer ' + token,
      },
      reconnectDelay: 5000,
    });

    this.stompClient.onConnect = () => {
      this.subscription?.unsubscribe(); // safety

      this.subscription = this.stompClient!.subscribe('/topic/activity', (message) => {
        const notification: NotificationModel = JSON.parse(message.body);
        const current = this.notifications$.value;
        this.notifications$.next([notification, ...current]);
        this.newNotification$.next(notification);
      });
    };

    this.stompClient.activate();
  }

  disconnect() {
    this.subscription?.unsubscribe();
    if (this.stompClient) {
      this.stompClient.deactivate(); // closes the connection
    }
  }
}