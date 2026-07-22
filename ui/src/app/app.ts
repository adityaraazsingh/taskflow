import { Component, inject, signal } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive, Router } from '@angular/router';
import { UserModel } from './core/models/user.model';
import { AuthService } from './core/services/auth.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('ui');
  protected sidebarOpen = false;
  router = inject(Router);

  user!: UserModel;
  loading = signal(true);
  constructor(private authService: AuthService) {
    // this.authService.me().subscribe(
    //   (next) => {
    //     console.log("Users is laoded ", next);
    //     this.user = next;
    //     this.user.createdAt = new Date(this.user.createdAt!);
    //     this.loading.set(false);
    //   }
    // )
  }

  protected toggleSidebar(): void {
    this.sidebarOpen = !this.sidebarOpen;
  }

  protected onLogout(): void {
    localStorage.removeItem('Token');
    this.router.navigate(['/login']);
    console.log('Logout clicked');
  }

  onProfileClick(){
    this.router.navigate(["/profile"]);
  }
}
