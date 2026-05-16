import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-dashboard-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.scss'
})
export class DashboardHomeComponent {
  private auth = inject(AuthService);

  username = this.auth.getUsername() ?? 'Utilizator';
  role = this.auth.getUserRole() ?? 'USER';
  isAdmin = this.role === 'ADMIN';
}
