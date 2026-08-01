import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from "../../environment";
// import SockJS from 'sockjs-client';
// import { Client, over } from 'stompjs';
// import Stomp from 'stompjs';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

import { CommentModel } from "../models/comment.model";

@Injectable({
    providedIn:'root'
})

export class CommentService {
  url: string = environment.apiUrl + '/comments';
  apiUrl = environment.apiUrl;


  constructor(private httpClient: HttpClient) {}

  private stompClient: Client | null = null;

  connect(onCommentReceived: (comment: CommentModel) => void) {
    this.stompClient = new Client({
      // If your backend supports raw WebSocket:
      brokerURL: environment.brokerUrl,

      // If you need SockJS fallback:
      webSocketFactory: () => new SockJS(environment.wsUrl),

      connectHeaders: {
        Authorization: 'Bearer ' + localStorage.getItem('accessToken'),
      },

      reconnectDelay: 5000,
    });

    this.stompClient.onConnect = () => {
      console.log('Connected to WebSocket');
      this.stompClient?.subscribe('/topic/comments', (message) => {
        onCommentReceived(JSON.parse(message.body));
      });
    };

    this.stompClient.activate();
  }

  public deleteComments(commentId: number) {
    return this.httpClient.delete(`${this.url}/${commentId}`);
  }
}
