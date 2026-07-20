import { Injectable } from "@angular/core";
import { UserModel } from "../models/user.model";
import { environment } from "../../environment";
import { HttpClient } from "@angular/common/http";

@Injectable({
    providedIn:'root'
})

export class UserService{
    
    url : string = environment.apiUrl;
    constructor(private httpClient:HttpClient){}


    public getUserWithUsername(username: string){
        return this.httpClient.get<UserModel>(
            `${this.url}/me/${username}`
        );
    }

    public getAllUsers(){
        return this.httpClient.get<UserModel>(
            `${this.url}/users/all`
        );
    }
}