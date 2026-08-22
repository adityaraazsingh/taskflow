import { Injectable } from "@angular/core";
import { environment } from "../../environment";
import { Client, StompSubscription } from "@stomp/stompjs";
import { CommentModel } from "../models/comment.model";
import { HttpClient } from "@angular/common/http";
import SockJS from "sockjs-client";
import { NotificationModel } from "../models/NotificationModel";
import { BehaviorSubject } from "rxjs";

@Injectable({
  providedIn: 'root'
})

export class NotificationService {
  private stompClient: Client | null = null;
  private subscription: StompSubscription | undefined;
  
  private url = environment.apiUrl;
  
  private notifications$ = new BehaviorSubject<NotificationModel[]>([]);
  public notificationsObs$ = this.notifications$.asObservable();

  constructor(private httpClient: HttpClient) { }

  loadNotifications() {
    this.httpClient.get<NotificationModel[]>(`${this.url}/activity`)
      .subscribe(data => this.notifications$.next(data));
  }

  connect() {
    if (this.stompClient?.active) return; 
    this.stompClient = new Client({
      webSocketFactory: () => new SockJS(environment.wsUrl),
      connectHeaders: {
        Authorization: 'Bearer ' + localStorage.getItem('accessToken'),
      },
      reconnectDelay: 5000,
    });

    this.stompClient.onConnect = () => {
      this.subscription?.unsubscribe(); // safety

      this.subscription = this.stompClient!.subscribe('/topic/activity', (message) => {
        const notification: NotificationModel = JSON.parse(message.body);

        const current = this.notifications$.value;
        this.notifications$.next([notification, ...current]);
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