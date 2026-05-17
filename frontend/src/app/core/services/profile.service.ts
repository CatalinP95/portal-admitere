import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { User } from '../models/user.model';
import { UserProfile, UserProfileRequest, ChangePasswordRequest } from '../models/user-profile.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ProfileService {
  private http = inject(HttpClient);
  private usersBase = `${environment.apiUrl}/api/users`;
  private profileBase = `${environment.apiUrl}/api/profile`;

  getMe(): Observable<User> {
    return this.http.get<User>(`${this.usersBase}/me`);
  }

  getOwnProfile(): Observable<UserProfile | null> {
    return this.http.get<UserProfile>(this.profileBase).pipe(
      catchError(err => {
        if (err.status === 404) return of(null);
        throw err;
      })
    );
  }

  saveProfile(request: UserProfileRequest): Observable<UserProfile> {
    return this.http.put<UserProfile>(this.profileBase, request);
  }

  changePassword(request: ChangePasswordRequest): Observable<void> {
    return this.http.put<void>(`${this.usersBase}/me/password`, request);
  }
}
