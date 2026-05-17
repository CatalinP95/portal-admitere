import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class DashboardComponent {
  private auth = inject(AuthService);
  private router = inject(Router);

  username = this.auth.getUsername() ?? 'Utilizator';
  role = this.auth.getUserRole() ?? 'USER';
  logoutLoading = false;
  isAdmin = this.role === 'ADMIN';

  logout(): void {
    this.logoutLoading = true;
    this.auth.logout().subscribe({
      next: () => this.router.navigate(['/login']),
      error: () => this.router.navigate(['/login'])
    });
  }
}
