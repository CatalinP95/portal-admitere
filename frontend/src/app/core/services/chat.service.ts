import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface ChatMessage {
  role: 'user' | 'bot';
  text: string;
  time: Date;
}

@Injectable({ providedIn: 'root' })
export class ChatService {
  private http = inject(HttpClient);
  private base = `${environment.apiUrl}/api/chat`;

  ask(message: string): Observable<{ reply: string }> {
    return this.http.post<{ reply: string }>(`${this.base}/ask`, { message });
  }
}
