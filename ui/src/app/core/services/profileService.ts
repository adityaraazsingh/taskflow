import { Injectable, OnInit, signal } from "@angular/core";
import { environment } from "../../environment";
import { HttpClient } from "@angular/common/http";
import { ProfileModel } from "../models/profile.model";
import { BehaviorSubject } from "rxjs";
import { AuthService } from "./auth.service";
import { UserModel } from "../models/user.model";

@Injectable({
    providedIn:'root'
})

export class ProfileService{
    url = environment.apiUrl+'/profile';
    public profileSignal = signal<ProfileModel | null>(null)
    currUser = signal<UserModel| null>(null);
    constructor(private httpClient : HttpClient, 
        private authService : AuthService
    ){
         this.currUser = this.authService.userSignal
    }

    getProfileByUserId(){
        return this.httpClient.get<ProfileModel>(`${this.url}/${this.currUser()?.id!}`).subscribe(
            (next)=>{
                this.profileSignal.set(next)
            }
        )
    }

    public saveProfileByUserId(profile : ProfileModel){
        return this.httpClient.put(`${this.url}`,profile)
    }
}