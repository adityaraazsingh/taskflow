import { Injectable } from "@angular/core";
import { environment } from "../../environment";
import { HttpClient } from "@angular/common/http";
import { TagModel } from "../models/tag.model";

@Injectable({
    providedIn:'root'
})

export class TagService{
    url : string = environment.apiUrl+'/tags';
    constructor(private httpClient:HttpClient){}

    public getTags(){
        return this.httpClient.get<TagModel[]>(`${this.url}`);
    }

    public postTags(tags : TagModel[]){
        return this.httpClient.post<TagModel[]>(`${this.url}`, tags);
    }
}