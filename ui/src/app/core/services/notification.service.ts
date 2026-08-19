import { Injectable } from "@angular/core";
import { environment } from "../../environment";
import { Client, StompSubscription } from "@stomp/stompjs";
import { CommentModel } from "../models/comment.model";
import { HttpClient } from "@angular/common/http";
import SockJS from "sockjs-client";

@Injectable({
    providedIn: 'root'
})

export class NotificationService{
    private subscription: StompSubscription | undefined;
    private stompClient: Client | null = null;
    constructor(private httpClient: HttpClient) {}
    
    connect(onCommentReceived: (comment: CommentModel) => void) {
        console.log("Connect function is called")
        this.stompClient = new Client({
        webSocketFactory: () => new SockJS(environment.wsUrl),

        connectHeaders: {
            Authorization: 'Bearer ' + localStorage.getItem('accessToken'),
        },

        reconnectDelay: 5000,
        });
    
        this.stompClient.onConnect = () => {
          console.log('Connected to WebSocket');
          this.subscription = this.stompClient?.subscribe('/topic/activity', (message) => {
            onCommentReceived(JSON.parse(message.body));
          });
        };
    
        this.stompClient.activate();
      }
    
      disconnect() {
        this.subscription?.unsubscribe();
        if (this.stompClient) {
          console.log('Disconnecting from WebSocket');
          this.stompClient.deactivate(); // closes the connection
        }
      }


}