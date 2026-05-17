import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthResponse, LoginRequest, RefreshRequest, RegisterRequest } from '../models/auth.models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private base = `${environment.apiUrl}/auth`;

  private readonly ACCESS_TOKEN_KEY = 'access_token';
  private readonly REFRESH_TOKEN_KEY = 'refresh_token';
  private readonly USERNAME_KEY = 'username';
  private readonly ROLE_KEY = 'role';

  login(req: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.base}/login`, req).pipe(
      tap(res => this.saveTokens(res, req.rememberMe ?? false))
    );
  }

  register(req: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.base}/register`, req).pipe(
      tap(res => this.saveTokens(res, false))
    );
  }

  logout(): Observable<void> {
    const refreshToken = this.getRefreshToken() ?? '';
    const headers = new HttpHeaders({ 'Refresh-Token': refreshToken });
    return this.http.post<void>(`${this.base}/logout`, {}, { headers }).pipe(
      tap(() => this.clearTokens())
    );
  }

  refresh(): Observable<AuthResponse> {
    const refreshToken = this.getRefreshToken();
    const req: RefreshRequest = { refreshToken: refreshToken! };
    return this.http.post<AuthResponse>(`${this.base}/refresh`, req).pipe(
      tap(res => this.saveTokens(res, !!localStorage.getItem(this.ACCESS_TOKEN_KEY)))
    );
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  getToken(): string | null {
    return localStorage.getItem(this.ACCESS_TOKEN_KEY)
      ?? sessionStorage.getItem(this.ACCESS_TOKEN_KEY);
  }

  getRefreshToken(): string | null {
    return localStorage.getItem(this.REFRESH_TOKEN_KEY)
      ?? sessionStorage.getItem(this.REFRESH_TOKEN_KEY);
  }

  getUsername(): string | null {
    return localStorage.getItem(this.USERNAME_KEY)
      ?? sessionStorage.getItem(this.USERNAME_KEY);
  }

  getUserRole(): string | null {
    return localStorage.getItem(this.ROLE_KEY)
      ?? sessionStorage.getItem(this.ROLE_KEY);
  }

  private saveTokens(res: AuthResponse, remember: boolean): void {
    const storage = remember ? localStorage : sessionStorage;
    storage.setItem(this.ACCESS_TOKEN_KEY, res.accessToken);
    storage.setItem(this.REFRESH_TOKEN_KEY, res.refreshToken);
    storage.setItem(this.USERNAME_KEY, res.username);
    storage.setItem(this.ROLE_KEY, res.role);
  }

  clearTokens(): void {
    [localStorage, sessionStorage].forEach(s => {
      s.removeItem(this.ACCESS_TOKEN_KEY);
      s.removeItem(this.REFRESH_TOKEN_KEY);
      s.removeItem(this.USERNAME_KEY);
      s.removeItem(this.ROLE_KEY);
    });
  }
}
