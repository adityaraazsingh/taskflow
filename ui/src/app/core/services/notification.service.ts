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

  private loadError = new BehaviorSubject<boolean>(false);
  public loadErrorObs$ = this.loadError.asObservable();

  constructor(private httpClient: HttpClient) { }

  ngOnInit() {
    this.loadNotifications();
  }

  loadNotifications() {
    this.httpClient.get<NotificationModel[]>(`${this.url}/activity`)
      .subscribe({
        next: data => {
          this.loadError.next(false);
          // Guard against non-array payloads (e.g. a paged { content: [...] } response)
          const list = Array.isArray(data)
            ? data
            : Array.isArray((data as any)?.content) ? (data as any).content : [];
          this.notifications$.next(list);
        },
        error: () => this.loadError.next(true)
      });
  }

  connect() {
    if (this.stompClient?.active) return; 
    const token = typeof window !== 'undefined'
      ? localStorage.getItem('accessToken')
      : null;
    this.stompClient = new Client({
      brokerURL: environment.brokerUrl, 
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