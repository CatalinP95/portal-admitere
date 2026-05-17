import { Component, signal, inject, ViewChild, ElementRef, AfterViewChecked } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChatService, ChatMessage } from '../../core/services/chat.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-chatbot',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chatbot.html',
  styleUrl: './chatbot.scss'
})
export class ChatbotComponent implements AfterViewChecked {
  @ViewChild('messageList') messageList!: ElementRef;

  private chatService = inject(ChatService);
  private authService = inject(AuthService);

  isOpen = signal(false);
  isLoading = signal(false);
  userInput = signal('');
  messages = signal<ChatMessage[]>([
    {
      role: 'bot',
      text: 'Bună! Sunt RoboAdmis 🤖 — asistentul tău virtual pentru admitere. Cu ce te pot ajuta?',
      time: new Date()
    }
  ]);

  get isLoggedIn(): boolean { return this.authService.isLoggedIn(); }

  private shouldScroll = false;

  toggle(): void {
    this.isOpen.update(v => !v);
  }

  setInput(value: string): void {
    this.userInput.set(value);
  }

  send(): void {
    const text = this.userInput().trim();
    if (!text || this.isLoading()) return;

    this.messages.update(msgs => [...msgs, { role: 'user', text, time: new Date() }]);
    this.userInput.set('');
    this.isLoading.set(true);
    this.shouldScroll = true;

    this.chatService.ask(text).subscribe({
      next: (res) => {
        this.messages.update(msgs => [...msgs, { role: 'bot', text: res.reply, time: new Date() }]);
        this.isLoading.set(false);
        this.shouldScroll = true;
      },
      error: () => {
        this.messages.update(msgs => [...msgs, {
          role: 'bot',
          text: 'Ups, ceva nu a mers... Încearcă din nou! 🔧',
          time: new Date()
        }]);
        this.isLoading.set(false);
        this.shouldScroll = true;
      }
    });
  }

  onEnter(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.send();
    }
  }

  ngAfterViewChecked(): void {
    if (this.shouldScroll && this.messageList) {
      const el = this.messageList.nativeElement;
      el.scrollTop = el.scrollHeight;
      this.shouldScroll = false;
    }
  }
}
