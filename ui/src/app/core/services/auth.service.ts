import { HttpClient, HttpRequest, HttpResponse } from "@angular/common/http";
import { environment } from "../../environment";
import { LoginRequest } from "../models/loginRequest.model";
import { Injectable ,OnInit,signal} from "@angular/core";
import { AuthModel } from "../models/auth.model";
import { UserModel } from "../models/user.model";
import { ChangePasswordDto } from "../models/ChangePasswordDto";
import { Router } from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class AuthService implements OnInit{
    url : string = environment.apiUrl;
    public isUserLoggedIn = signal<boolean | null>(null)

    constructor(private httpClient:HttpClient, private router : Router){}

    ngOnInit(): void {
        this.checkIfUserLoggedIn();
    }

    checkIfUserLoggedIn(){
        const isLoggedIn = !!localStorage.getItem('Token'); 
        if (isLoggedIn) {
            this.isUserLoggedIn.set(true);
        } else {
            this.isUserLoggedIn.set(false);
        }
    }

    
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

    public changePassword(changePasswordReq : ChangePasswordDto){
        return this.httpClient.post(`${this.url}/auth/password`,changePasswordReq);
    }

    // Auth.controller Done
    public signUp(payload: UserModel){
        return this.httpClient.post<UserModel>(
            `${this.url}/users/signup`,
            payload
        );
    }

}