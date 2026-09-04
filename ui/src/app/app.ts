import { Component, inject, signal } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive, Router } from '@angular/router';
import { UserModel } from './core/models/user.model';
import { AuthService } from './core/services/auth.service';
import { NotificationDialog } from "./shared/components/notification-dialog/notification-dialog";

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, NotificationDialog],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('ui');
  protected sidebarOpen = false;
  protected notificationDialogOpen = false;
  router = inject(Router);

  user = signal<UserModel | null>(null);
  loading = signal(true);
  constructor(private authService: AuthService) {
    if(authService.isUserLoggedIn()){
      // this.authService.me().subscribe(
      //   (next) => {
      //     console.log("Users is laoded ", next);
      //     this.user.set(next);
      //     this.user()!.createdAt = new Date(this.user()!.createdAt!);
      //     this.loading.set(false);
      //   }
      // )
    }
  }

  protected toggleSidebar(): void {
    this.sidebarOpen = !this.sidebarOpen;
  }

  protected onLogout(): void {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    this.router.navigate(['/login']);
    console.log('Logout clicked');
  }

  openNotificationDialog(){
    this.notificationDialogOpen = !this.notificationDialogOpen;
  }

  onProfileClick(){
    this.router.navigate(["/profile"]);
  }
}
