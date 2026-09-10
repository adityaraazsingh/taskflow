import { Component, inject, signal } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive, Router } from '@angular/router';
import { UserModel } from './core/models/user.model';
import { AuthService } from './core/services/auth.service';
import { NotificationDialog } from "./shared/components/notification-dialog/notification-dialog";
import { ProjectService } from './core/services/project.service';
import { ProfileService } from './core/services/profileService';
import { ProfileModel } from './core/models/profile.model';

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
  firstNameProfile = signal<ProfileModel>({
    id: 0,
    userId: 0,
    firstName: '',  
    lastName: '',
    bio: '',
    avatarUrl: ''
  });
  router = inject(Router);


  user = signal<UserModel | null>(null);
  loading = signal(true);
  constructor(private authService: AuthService, private profileService : ProfileService) {
    this.firstNameProfile = this.profileService.profileSignal;
    authService.me()
  }

  protected toggleSidebar(): void {
    this.sidebarOpen = !this.sidebarOpen;
  }

  protected onLogout(): void {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    this.router.navigate(['/login']);
  }

  openNotificationDialog(){
    this.notificationDialogOpen = !this.notificationDialogOpen;
  }

  onProfileClick(){
    this.router.navigate(["/profile"]);
  }
}
