import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from "../../environment";
import SockJS from 'sockjs-client';
// import { Client, over } from 'stompjs';
import Stomp from 'stompjs';
import { CommentModel } from "../models/comment.model";

@Injectable({
    providedIn:'root'
})

export class CommentService{
    url : string = environment.apiUrl + '/comments';
    constructor(private httpClient:HttpClient){}

    private stompClient: Stomp.Client | null = null;
    

    connect(onCommentReceived: (comment: CommentModel) => void) {
        const socket = new SockJS('http://localhost:8080/ws'); // backend endpoint
        this.stompClient = Stomp.over(socket);

        this.stompClient.connect(
            { Authorization: 'Bearer ' + localStorage.getItem('token') }, () => {
        console.log('Connected to WebSocket');
        this.stompClient?.subscribe('/topic/comments', (message) => {
            onCommentReceived(JSON.parse(message.body));
        });
        });
    }

    public deleteComments(commentId : number){
        return this.httpClient.delete(`${this.url}/${commentId}`)
    }
}