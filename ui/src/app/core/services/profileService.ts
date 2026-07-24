import { Injectable } from "@angular/core";
import { environment } from "../../environment";
import { HttpClient } from "@angular/common/http";
import { ProfileModel } from "../models/profile.model";

@Injectable({
    providedIn:'root'
})

export class ProfileService{
    url = environment.apiUrl+'/profile';

    constructor(private httpClient : HttpClient){}

    getProfileByUserId(userId : number){
        return this.httpClient.get(`${this.url}/${userId}`)
    }

    public saveProfileByUserId(profile : ProfileModel){
        return this.httpClient.put(`${this.url}`,profile)
    }
}