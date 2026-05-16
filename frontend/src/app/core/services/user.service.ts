import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Page } from '../models/page.model';
import { User, UserRequest } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class UserService {
  private http = inject(HttpClient);
  private base = 'http://localhost:8080/api/users';

  getAll(page = 0, size = 10, sort = 'username,asc', search?: string, role?: string): Observable<Page<User>> {
    let params = new HttpParams().set('page', page).set('size', size).set('sort', sort);
    if (search) params = params.set('search', search);
    if (role) params = params.set('role', role);
    return this.http.get<Page<User>>(this.base, { params });
  }

  getById(id: number): Observable<User> {
    return this.http.get<User>(`${this.base}/${id}`);
  }

  create(request: UserRequest): Observable<User> {
    return this.http.post<User>(this.base, request);
  }

  update(id: number, request: UserRequest): Observable<User> {
    return this.http.put<User>(`${this.base}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }

  changeRole(id: number, role: string): Observable<User> {
    const params = new HttpParams().set('role', role);
    return this.http.put<User>(`${this.base}/${id}/role`, null, { params });
  }
}
