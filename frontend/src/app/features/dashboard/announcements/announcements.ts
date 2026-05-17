import { Component, inject, OnInit, ChangeDetectorRef, DestroyRef } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CommonModule } from '@angular/common';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { Subject, timer, debounceTime, distinctUntilChanged } from 'rxjs';
import { AnnouncementService } from '../../../core/services/announcement.service';
import { AuthService } from '../../../core/services/auth.service';
import { Announcement, AnnouncementRequest, AVAILABLE_TAGS } from '../../../core/models/announcement.model';
import { Page } from '../../../core/models/page.model';

@Component({
  selector: 'app-announcements',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './announcements.html',
  styleUrl: './announcements.scss'
})
export class AnnouncementsComponent implements OnInit {
  private service = inject(AnnouncementService);
  private auth = inject(AuthService);
  private fb = inject(FormBuilder);
  private cdr = inject(ChangeDetectorRef);
  private destroyRef = inject(DestroyRef);

  isAdmin = this.auth.getUserRole() === 'ADMIN';
  availableTags = [...AVAILABLE_TAGS];

  page: Page<Announcement> | null = null;
  loading = false;
  error = '';
  currentPage = 0;
  activeTag = '';
  searchTerm = '';
  sortField = 'createdAt';
  sortDir: 'asc' | 'desc' = 'desc';

  private searchSubject = new Subject<string>();

  showModal = false;
  editingId: number | null = null;
  modalLoading = false;
  modalError = '';
  selectedTags: string[] = [];

  form = this.fb.group({
    title: ['', [Validators.required, Validators.maxLength(200)]],
    content: ['', Validators.required]
  });

  get title() { return this.form.get('title')!; }
  get content() { return this.form.get('content')!; }
  get sortParam(): string { return `${this.sortField},${this.sortDir}`; }

  ngOnInit(): void {
    this.load();

    this.searchSubject.pipe(
      debounceTime(400),
      distinctUntilChanged(),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(() => { this.currentPage = 0; this.load(); });

    timer(30000, 30000).pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(() => this.load());
  }

  load(): void {
    this.loading = true;
    this.error = '';
    this.service.getAll(
      this.currentPage, 9,
      this.activeTag || undefined,
      this.sortParam,
      this.searchTerm || undefined
    ).subscribe({
      next: p => { this.page = p; this.loading = false; this.cdr.detectChanges(); },
      error: () => { this.error = 'Eroare la încărcarea anunțurilor.'; this.loading = false; this.cdr.detectChanges(); }
    });
  }

  onSearch(term: string): void {
    this.searchTerm = term;
    this.searchSubject.next(term);
  }

  sort(field: string): void {
    if (this.sortField === field) {
      this.sortDir = this.sortDir === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortField = field;
      this.sortDir = 'asc';
    }
    this.currentPage = 0;
    this.load();
  }

  filterByTag(tag: string): void {
    this.activeTag = this.activeTag === tag ? '' : tag;
    this.currentPage = 0;
    this.load();
  }

  goToPage(p: number): void {
    this.currentPage = p;
    this.load();
  }

  openCreate(): void {
    this.editingId = null;
    this.form.reset();
    this.selectedTags = [];
    this.modalError = '';
    this.showModal = true;
  }

  openEdit(a: Announcement): void {
    this.editingId = a.id;
    this.form.patchValue({ title: a.title, content: a.content });
    this.selectedTags = [...a.tags];
    this.modalError = '';
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
    this.editingId = null;
  }

  toggleTag(tag: string): void {
    const idx = this.selectedTags.indexOf(tag);
    if (idx >= 0) this.selectedTags.splice(idx, 1);
    else this.selectedTags.push(tag);
  }

  submit(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid) return;

    const request: AnnouncementRequest = {
      title: this.form.value.title!,
      content: this.form.value.content!,
      tagNames: this.selectedTags
    };

    this.modalLoading = true;
    this.modalError = '';

    const op$ = this.editingId
      ? this.service.update(this.editingId, request)
      : this.service.create(request);

    op$.subscribe({
      next: () => {
        this.showModal = false;
        this.editingId = null;
        this.modalLoading = false;
        this.cdr.detectChanges();
        this.load();
      },
      error: () => { this.modalError = 'Eroare. Încearcă din nou.'; this.modalLoading = false; this.cdr.detectChanges(); }
    });
  }

  delete(id: number): void {
    if (!confirm('Sigur vrei să ștergi acest anunț?')) return;
    this.service.delete(id).subscribe({
      next: () => this.load(),
      error: () => this.error = 'Eroare la ștergere.'
    });
  }
}
