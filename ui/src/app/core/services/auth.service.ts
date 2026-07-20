import { HttpClient, HttpRequest, HttpResponse } from "@angular/common/http";
import { environment } from "../../environment";
import { LoginRequest } from "../models/loginRequest.model";
import { Injectable } from "@angular/core";
import { AuthModel } from "../models/auth.model";
import { UserModel } from "../models/user.model";

@Injectable({
  providedIn: 'root'
})
export class AuthService{
    url : string = environment.apiUrl;
    constructor(private httpClient:HttpClient){}

    public login(payload : LoginRequest){
        return this.httpClient.post<AuthModel>(
            `${this.url}/auth/login`,
            payload
        );
    }

    
    public me(){
        return this.httpClient.get<UserModel>(
            `${this.url}/auth/me`
        );
    }
    // Auth.controller Done
    public signUp(payload: UserModel){
        return this.httpClient.post<UserModel>(
            `${this.url}/users/signup`,
            payload
        );
    }

}