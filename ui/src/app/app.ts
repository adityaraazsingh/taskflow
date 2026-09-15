import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive, Router } from '@angular/router';
import { UserModel } from './core/models/user.model';
import { AuthService } from './core/services/auth.service';
import { NotificationDialog } from "./shared/components/notification-dialog/notification-dialog";
import { ProjectService } from './core/services/project.service';
import { ProfileService } from './core/services/profileService';
import { ProfileModel } from './core/models/profile.model';
import { NotificationService } from './core/services/notification.service';
import { NotificationModel } from './core/models/NotificationModel';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, NotificationDialog],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
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
  snackBar = inject(MatSnackBar);

  user = signal<UserModel | null>(null);
  loading = signal(true);
  constructor(private authService: AuthService, private profileService: ProfileService, private notificationService: NotificationService) {
    this.firstNameProfile = this.profileService.profileSignal;
    this.authService.me()
  }

  ngOnInit() {
    this.notificationService.newNotificationObs$.subscribe(notification => {
      console.log('New notification received:', notification);
      this.triggerPopup(notification);
    });
    this.notificationService.connect();
    this.notificationService.loadNotifications();

  }

  triggerPopup(notification: NotificationModel) {
    this.snackBar.open(
      notification.message || 'New notification',
      'View',
      {
        duration: 4000
      }
    ).onAction().subscribe();
  }

  protected toggleSidebar(): void {
    this.sidebarOpen = !this.sidebarOpen;
  }

  protected onLogout(): void {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    this.router.navigate(['/login']);
  }

  openNotificationDialog() {
    this.notificationDialogOpen = !this.notificationDialogOpen;
  }

  onProfileClick() {
    this.router.navigate(["/profile"]);
  }
}
