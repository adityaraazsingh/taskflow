import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from "../../environment";
import { Client, StompSubscription } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { CommentModel } from "../models/comment.model";
import { BehaviorSubject, forkJoin } from "rxjs";
import { TaskService } from "./task.service";

@Injectable({
    providedIn:'root'
})

export class CommentService {
  url: string = environment.apiUrl + '/comments';
  private taskSubscriptions = new Map<number, StompSubscription>();
  private commentsMap$ = new BehaviorSubject<Map<number, CommentModel[]>>(new Map());
  public comments$ = this.commentsMap$.asObservable();

  constructor(private httpClient: HttpClient, private TaskService : TaskService) {}

  private stompClient: Client | null = null;

  loadCommentsForAllTasks(taskIds: number[]) {
    const requests = taskIds.map(taskId =>
      this.TaskService.getCommentsForTask(taskId, 0, 10)
    );

    forkJoin(requests).subscribe(results => {
      const map = new Map(this.commentsMap$.value);

      results.forEach((res, index) => {
        const taskId = taskIds[index];
        map.set(taskId, res.content);
      });

      this.commentsMap$.next(map);
    });
  }

  connect() {
    this.stompClient = new Client({
      webSocketFactory: () => new SockJS(environment.wsUrl),
      connectHeaders: {
        Authorization: 'Bearer ' + localStorage.getItem('accessToken'),
      },
      reconnectDelay: 5000,
    });

    this.stompClient.onConnect = () => {
      console.log("WS Connected");
    };

    this.stompClient.activate();
  }

  connectToTask(taskId: number) {
    console.log("Trying to subscribe. Connected?", this.stompClient?.connected);
    if (!this.stompClient || !this.stompClient.connected) {
      console.log("Not connected yet");
      return;
    }

    const sub = this.stompClient.subscribe(
      `/topic/comments/${taskId}`,
      (message) => {
        const incoming: CommentModel[] = JSON.parse(message.body);

        const map = new Map(this.commentsMap$.value);
        const existing = map.get(taskId) || [];

        const updated = [...existing, ...incoming]
          .filter((v, i, arr) => arr.findIndex(c => c.id === v.id) === i);

        map.set(taskId, updated);
        this.commentsMap$.next(map);
      }
    );

    this.taskSubscriptions.set(taskId, sub);
  }

  unsubscribeTask(taskId: number) {
    this.taskSubscriptions.get(taskId)?.unsubscribe();
    this.taskSubscriptions.delete(taskId);
  }

  public deleteComments(commentId: number) {
    return this.httpClient.delete(`${this.url}/${commentId}`);
  }
}
