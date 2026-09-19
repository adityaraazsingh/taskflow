import { Component, inject, signal } from '@angular/core';
import { FormControl, FormControlName, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { LoginRequest } from '../../../core/models/loginRequest.model';
import { UserModel } from '../../../core/models/user.model';
import { Router } from '@angular/router';
import { UserService } from '../../../core/services/user.service';
import { MatButtonModule } from '@angular/material/button';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { SignUpUserDto } from '../../../core/models/SignUpUserDto';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, MatButtonModule, MatSlideToggleModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {

  isLoggingIn : boolean = true;
  isLoading = signal(false);
  // user$ = new BehaviorSubject<UserModel | null>(null);
  // userObservable$ = this.user$.asObservable();
  
  loginForm = new FormGroup({
    username : new FormControl('DefaultTENANT/admin'),
    password : new FormControl('Admin@123'),
  });

  signUpForm = new FormGroup({
    tenantName : new FormControl(''),
    username : new FormControl(''),
    password : new FormControl(''),
    role : new FormControl(''),
    email: new FormControl('')
  });

  authService = inject(AuthService);
  userService = inject(UserService);
  router = inject(Router);

  onLoginClick(){
    if(this.isLoading()) return;
    if(this.isLoggingIn){
      const payload:LoginRequest = {
        username: this.loginForm.value.username!,
        password: this.loginForm.value.password!
      };
      this.isLoading.set(true);
      this.authService.login(payload).subscribe({
        next: () => {
          this.isLoading.set(false);
          this.authService.checkIfUserLoggedIn();
          this.authService.me();
          this.router.navigate(['/dashboard']);
        },
        error: () => this.isLoading.set(false)
      });
    }else{
      const payload : SignUpUserDto = {
        tenantName: this.signUpForm.value.tenantName!,
        username: this.signUpForm.value.username!,
        password: this.signUpForm.value.password!,
        role : this.signUpForm.value.role!,
        email:this.signUpForm.value.email!
      };
      this.isLoading.set(true);
      this.authService.signUp(payload).subscribe({
        next : (response) => {
          window.alert(`User signed up successfully ${response}`);
          this.isLoading.set(false);
        },
        error : (error) => {
          console.error(error);
          this.isLoading.set(false);
        }
      });
    }
  }

  onClick(){
    this.isLoggingIn = !this.isLoggingIn;
    this.loginForm.reset();
    this.signUpForm.reset();
  }

  toggleRole(event : any){
    console.log("Role toggle event:", event);
  }
}
