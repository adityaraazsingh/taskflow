import { Injectable } from "@angular/core";
import { UserModel } from "../models/user.model";
import { environment } from "../../environment";
import { HttpClient, HttpParams } from "@angular/common/http";
import { PageResponse } from "../models/PageResponse";

@Injectable({
    providedIn:'root'
})

export class UserService{
    
    url : string = environment.apiUrl+'/users';
    constructor(private httpClient:HttpClient){}


    public getUserWithUserId(userId: number){
        return this.httpClient.get<UserModel>(
            `${this.url}/${userId}`
        );
    }

    public getUserWithUsername(username: string){
        return this.httpClient.get<UserModel>(
            `${this.url}/me/${username}`
        );
    }

    public getAllUsers(page : number, size : number){
        let params = new HttpParams()
            .set('page', page.toString())
            .set('size', size.toString());

        return this.httpClient.get<PageResponse<UserModel>>(
            `${this.url}/all` , {params}
        );
    }
}