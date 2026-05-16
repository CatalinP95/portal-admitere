import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./features/auth/register/register').then(m => m.RegisterComponent)
  },
  {
    path: 'dashboard',
    canActivate: [authGuard],
    loadComponent: () => import('./features/dashboard/dashboard').then(m => m.DashboardComponent),
    children: [
      {
        path: '',
        loadComponent: () => import('./features/dashboard/home/home').then(m => m.DashboardHomeComponent)
      },
      {
        path: 'announcements',
        loadComponent: () => import('./features/dashboard/announcements/announcements').then(m => m.AnnouncementsComponent)
      },
      {
        path: 'users',
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] },
        loadComponent: () => import('./features/dashboard/users/users').then(m => m.UsersComponent)
      },
      {
        path: 'profile',
        loadComponent: () => import('./features/dashboard/profile/profile').then(m => m.ProfileComponent)
      }
    ]
  },
  {
    path: 'unauthorized',
    loadComponent: () => import('./features/errors/unauthorized').then(m => m.UnauthorizedComponent)
  },
  {
    path: '**',
    loadComponent: () => import('./features/errors/not-found').then(m => m.NotFoundComponent)
  }
];
