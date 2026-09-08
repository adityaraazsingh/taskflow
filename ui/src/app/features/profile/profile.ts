import { Component, effect, OnInit, output, signal } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { UserModel } from '../../core/models/user.model';
import { AbstractControl, FormControl, FormControlName, FormGroup, ReactiveFormsModule, ValidationErrors } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { LoadingSpinner } from "../../shared/components/loading-spinner/loading-spinner";
import { UserService } from '../../core/services/user.service';
import { ChangePasswordDto } from '../../core/models/ChangePasswordDto';
import { ProfileModel } from '../../core/models/profile.model';
import { ProfileService } from '../../core/services/profileService';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-profile',
  imports: [ReactiveFormsModule, DatePipe, LoadingSpinner],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class Profile implements OnInit{
  user = signal<UserModel|null>(null);
  profile = signal<ProfileModel | null>(null);
  loading = signal(true);
  isPasswordSame = signal(false);
  firstName = output<String>();

  profileForm = new FormGroup({
    avatarUrl : new FormControl(''),
    firstName : new FormControl(),
    lastName : new FormControl(),
    bio : new FormControl(),
  })

  constructor(private authService : AuthService, private userService : UserService, private profileService : ProfileService){
    this.profileService.getProfileByUserId();
    this.profile = this.profileService.profileSignal;
    this.user.set(this.authService.userSignal())
    effect(() => {
      const profile = this.profile();
      if (profile) {
        this.patchingValue()
      }
    });
  }
  
  ngOnInit(): void {
    this.firstName.emit(this.profile()?.firstName!);
    this.patchingValue()
    this.loading.set(false)
  }

  patchingValue(){
    this.profileForm.patchValue(this.profile()!)
  }

  changePasswordForm = new FormGroup({
    currentPassword : new FormControl(),
    newPassword : new FormControl(),
    confirmPassword : new FormControl(),

  },{validators: this.checkPasswordIsSame});

  checkPasswordIsSame(group : AbstractControl): ValidationErrors | null{
    const newPassword = group.get('newPassword')?.value;
    const confirmPassword = group.get('confirmPassword')?.value;

    return newPassword === confirmPassword ? null : { notSame: true };
  }

  onClickUpdatePassword(){
    const payload :  ChangePasswordDto= {
      currentPassword : this.changePasswordForm.controls.currentPassword.value,
      newPassword : this.changePasswordForm.controls.newPassword.value
    }
    this.authService.changePassword(payload).subscribe(
      (data)=>{
        window.alert("Password Changed Succefully")
      },
      (err)=>{
        window.alert("Something went wrong"),
        console.log(err)
      }
    );
  }

  onSaveButtonClick(){
    const payload :  ProfileModel ={
      firstName : this.profileForm.controls.firstName.value,
      lastName : this.profileForm.controls.lastName.value,
      bio : this.profileForm.controls.bio.value,
      userId : this.user()!.id,
      avatarUrl : ' url '
    }
    this.profileService.saveProfileByUserId(payload).subscribe(
      (next) =>{
        console.log(next)
      }
    )
  }
}
