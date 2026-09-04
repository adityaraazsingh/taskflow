import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from "../../environment";
import { NotificationModel } from "../models/NotificationModel";

@Injectable({
    providedIn: 'root'
})

export class ActivityService {
    url : string;
    constructor(private httpClient : HttpClient) {
        this.url = environment.apiUrl + '/activity';
    }

    public getActivity() {
        return this.httpClient.get<NotificationModel[]>(this.url);
    }
}