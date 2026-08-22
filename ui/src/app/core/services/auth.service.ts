import { HttpClient, HttpRequest, HttpResponse } from "@angular/common/http";
import { environment } from "../../environment";
import { LoginRequest } from "../models/loginRequest.model";
import { Injectable ,OnInit,signal} from "@angular/core";
import { AuthModel } from "../models/auth.model";
import { UserModel } from "../models/user.model";
import { ChangePasswordDto } from "../models/ChangePasswordDto";
import { Router } from "@angular/router";
import { BehaviorSubject } from "rxjs";
import { UserService } from "./user.service";
import { ProfileService } from "./profileService";

@Injectable({
  providedIn: 'root'
})
export class AuthService implements OnInit{
    url : string = environment.apiUrl;
    public userSignal = signal<UserModel | null>(null);
    public isUserLoggedIn = signal<boolean | null>(null)

    constructor(private httpClient:HttpClient, private router : Router, private userService : UserService, private profileService : ProfileService){}

    ngOnInit(): void {
        this.checkIfUserLoggedIn();
    }

    checkIfUserLoggedIn(){
        const isLoggedIn = !!localStorage.getItem('accessToken'); 
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
        ).subscribe({
        next : (response) => {
          localStorage.setItem("accessToken" , response.accessToken);
          localStorage.setItem("refreshToken" , response.refreshToken);
          this.checkIfUserLoggedIn()
          this.router.navigate(['/dashboard']);
          this.userService.getUserWithUsername(response.username).subscribe((data)=>{
                this.userSignal.set(data)
                this.profileService.getProfileByUserId(data.id!)
          })
        },
        error : (error) => {
          console.error(error);
        }
      });
    }

    
    public me(){
        return this.userSignal()
    }

    public refresh(){
        const refreshToken = localStorage.getItem("refreshToken");
        return this.httpClient.post<AuthModel>(
            `${this.url}/auth/refresh`, {"refreshToken" : refreshToken}
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