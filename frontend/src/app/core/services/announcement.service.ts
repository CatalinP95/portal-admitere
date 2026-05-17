import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Page } from '../models/page.model';
import { Announcement, AnnouncementRequest } from '../models/announcement.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AnnouncementService {
  private http = inject(HttpClient);
  private base = `${environment.apiUrl}/api/announcements`;

  getAll(page = 0, size = 9, tag?: string, sort = 'createdAt,desc', search?: string): Observable<Page<Announcement>> {
    let params = new HttpParams().set('page', page).set('size', size).set('sort', sort);
    if (tag) params = params.set('tag', tag);
    if (search) params = params.set('search', search);
    return this.http.get<Page<Announcement>>(this.base, { params });
  }

  create(request: AnnouncementRequest): Observable<Announcement> {
    return this.http.post<Announcement>(this.base, request);
  }

  update(id: number, request: AnnouncementRequest): Observable<Announcement> {
    return this.http.put<Announcement>(`${this.base}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
