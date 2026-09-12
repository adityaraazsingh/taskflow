import { afterEveryRender, Injectable, OnInit, signal } from "@angular/core";
import { environment } from "../../environment";
import { HttpClient } from "@angular/common/http";
import { ProfileModel } from "../models/profile.model";
import { BehaviorSubject } from "rxjs";
import { AuthService } from "./auth.service";
import { UserModel } from "../models/user.model";

@Injectable({
    providedIn: 'root'
})

export class ProfileService {
    url = environment.apiUrl + '/profile';
    public profileSignal = signal<ProfileModel>({
        id: 0,
        userId: 0,
        firstName: '',
        lastName: '',
        bio: '',
        avatarUrl: ''
    });
    currUser = signal<UserModel | null>(null);
    constructor(private httpClient: HttpClient) {}

    getProfileByUserId(userId: number) {
        if(isNaN(userId)){
            return;
        }
        this.httpClient.get<ProfileModel>(`${this.url}/${userId}`).subscribe(
            (next) => {
                this.profileSignal.set(next)
            }
        )
    }

    getProfileByUserDetails(userId: number) {
        return this.httpClient.get<ProfileModel>(`${this.url}/${userId}`);
    }

    public saveProfileByUserId(profile: ProfileModel) {
        return this.httpClient.put(`${this.url}`, profile)
    }
}