import { Component, inject, OnInit, ChangeDetectorRef, DestroyRef } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CommonModule } from '@angular/common';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { Subject, debounceTime, distinctUntilChanged } from 'rxjs';
import { UserService } from '../../../core/services/user.service';
import { User, UserRequest, ALL_ROLES } from '../../../core/models/user.model';
import { Page } from '../../../core/models/page.model';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './users.html',
  styleUrl: './users.scss'
})
export class UsersComponent implements OnInit {
  private service = inject(UserService);
  private fb = inject(FormBuilder);
  private cdr = inject(ChangeDetectorRef);
  private destroyRef = inject(DestroyRef);

  roles = ALL_ROLES;
  page: Page<User> | null = null;
  loading = false;
  error = '';
  currentPage = 0;

  searchTerm = '';
  roleFilter = '';
  sortField = 'username';
  sortDir: 'asc' | 'desc' = 'asc';

  private searchSubject = new Subject<string>();

  showModal = false;
  editingId: number | null = null;
  modalLoading = false;
  modalError = '';

  form = this.fb.group({
    username: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]]
  });

  get username() { return this.form.get('username')!; }
  get email() { return this.form.get('email')!; }
  get password() { return this.form.get('password')!; }
  get sortParam(): string { return `${this.sortField},${this.sortDir}`; }

  ngOnInit(): void {
    this.load();

    this.searchSubject.pipe(
      debounceTime(400),
      distinctUntilChanged(),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(() => { this.currentPage = 0; this.load(); });
  }

  load(): void {
    this.loading = true;
    this.error = '';
    this.service.getAll(
      this.currentPage, 10,
      this.sortParam,
      this.searchTerm || undefined,
      this.roleFilter || undefined
    ).subscribe({
      next: p => { this.page = p; this.loading = false; this.cdr.detectChanges(); },
      error: () => { this.error = 'Eroare la încărcarea utilizatorilor.'; this.loading = false; this.cdr.detectChanges(); }
    });
  }

  onSearch(term: string): void {
    this.searchTerm = term;
    this.searchSubject.next(term);
  }

  onRoleFilter(role: string): void {
    this.roleFilter = role;
    this.currentPage = 0;
    this.load();
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

  goToPage(p: number): void {
    this.currentPage = p;
    this.load();
  }

  openCreate(): void {
    this.editingId = null;
    this.form.reset();
    this.modalError = '';
    this.showModal = true;
  }

  openEdit(u: User): void {
    this.editingId = u.id;
    this.form.patchValue({ username: u.username, email: u.email, password: '' });
    this.modalError = '';
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
    this.editingId = null;
  }

  submit(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid) return;

    const request: UserRequest = {
      username: this.form.value.username!,
      email: this.form.value.email!,
      password: this.form.value.password!
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
      error: err => {
        this.modalError = err.status === 400 ? 'Utilizatorul sau emailul există deja.' : 'Eroare. Încearcă din nou.';
        this.modalLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  changeRole(id: number, role: string): void {
    this.service.changeRole(id, role).subscribe({
      next: () => this.load(),
      error: () => this.error = 'Eroare la schimbarea rolului.'
    });
  }

  delete(id: number): void {
    if (!confirm('Sigur vrei să dezactivezi acest utilizator?')) return;
    this.service.delete(id).subscribe({
      next: () => this.load(),
      error: () => this.error = 'Eroare la dezactivare.'
    });
  }
}
