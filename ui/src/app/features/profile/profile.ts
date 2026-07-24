import { Component, signal } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { UserModel } from '../../core/models/user.model';
import { AbstractControl, FormControl, FormControlName, FormGroup, ReactiveFormsModule, ValidationErrors } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { LoadingSpinner } from "../../shared/components/loading-spinner/loading-spinner";
import { UserService } from '../../core/services/user.service';
import { ChangePasswordDto } from '../../core/models/ChangePasswordDto';

@Component({
  selector: 'app-profile',
  imports: [ReactiveFormsModule, DatePipe, LoadingSpinner],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class Profile {
  user! : UserModel;
  loading = signal(true);
  isPasswordSame = signal(false);

  constructor(private authService : AuthService, private userService : UserService){
    this.authService.me().subscribe(
      (next)=>{
        console.log("Users is laoded ",next);
        this.user = next;
        this.user.createdAt = new Date(this.user.createdAt!);
        this.loading.set(false);
      }
    )
  }

  userForm = new FormGroup({

  })

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
}
