import { Component, inject } from '@angular/core';
import { FormControl, FormControlName, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { LoginRequest } from '../../../core/models/loginRequest.model';
import { UserModel } from '../../../core/models/user.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {

  isLoggingIn : boolean = true;
  
  loginForm = new FormGroup({
    username : new FormControl(''),
    password : new FormControl('123123'),
  });

  signUpForm = new FormGroup({
    username : new FormControl(''),
    password : new FormControl('123123'),
    role : new FormControl(''),
    email: new FormControl('')
  });

  authService = inject(AuthService);
  router = inject(Router);

  onLoginClick(){
    if(this.isLoggingIn){
      const payload:LoginRequest = {
        username: this.loginForm.value.username!,
        password: this.loginForm.value.password!
      };
      console.log(payload);
      this.authService.login(payload).subscribe({
        next : (response) => {
          localStorage.setItem("Token" , response.token);
          this.router.navigate(['/dashboard']);
        },
        error : (error) => {
          console.error(error);
        }
      });
    }else{
      const payload : UserModel = {
        username: this.signUpForm.value.username!,
        password: this.signUpForm.value.password!,
        role : this.signUpForm.value.role!,
        email:this.signUpForm.value.email!
      };
      console.log(payload);
      this.authService.signUp(payload).subscribe({
        next : (response) => {
          window.alert(`User signed up successfully ${response}`);
        },
        error : (error) => {
          console.error(error);
        }
      });
    }
  }

  onClick(){
    this.isLoggingIn = !this.isLoggingIn;
    this.loginForm.reset();
    this.signUpForm.reset();
  }

}
