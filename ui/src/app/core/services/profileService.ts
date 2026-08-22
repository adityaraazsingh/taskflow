import { Injectable, signal } from "@angular/core";
import { environment } from "../../environment";
import { HttpClient } from "@angular/common/http";
import { ProfileModel } from "../models/profile.model";
import { BehaviorSubject } from "rxjs";

@Injectable({
    providedIn:'root'
})

export class ProfileService{
    url = environment.apiUrl+'/profile';
    public profileSignal = signal<ProfileModel | null>(null)

    constructor(private httpClient : HttpClient){}

    getProfileByUserId(userId : number){
        return this.httpClient.get<ProfileModel>(`${this.url}/${userId}`).subscribe(
            (next)=>{
                this.profileSignal.set(next)
            }
        )
    }

    public saveProfileByUserId(profile : ProfileModel){
        return this.httpClient.put(`${this.url}`,profile)
    }
}