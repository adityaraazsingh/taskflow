import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from "../../environment";

@Injectable({
    providedIn:'root'
})

export class CommentService{
    url : string = environment.apiUrl + '/comments';
    constructor(private httpClient:HttpClient){}

    public deleteComments(commentId : number){
        return this.httpClient.delete(`${this.url}/${commentId}`)
    }
}