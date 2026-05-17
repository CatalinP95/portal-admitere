import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, AbstractControl } from '@angular/forms';
import { ProfileService } from '../../../core/services/profile.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './profile.html',
  styleUrl: './profile.scss'
})
export class ProfileComponent implements OnInit {
  private service = inject(ProfileService);
  private auth    = inject(AuthService);
  private fb      = inject(FormBuilder);
  private cdr     = inject(ChangeDetectorRef);

  /* ── Date cont (din localStorage — instant, fără loading) ── */
  username  = this.auth.getUsername()  ?? '—';
  role      = this.auth.getUserRole()  ?? '—';
  email     = '—';
  createdAt = '—';

  /* ── Date cont extinse (din /api/users/me) ── */
  meLoading = true;

  /* ── Formular date personale ── */
  profileForm = this.fb.group({
    firstName:   ['', [Validators.required, Validators.maxLength(50)]],
    lastName:    ['', [Validators.required, Validators.maxLength(50)]],
    cnp:         ['', [Validators.pattern(/^[0-9]{13}$/)]],
    dateOfBirth: [''],
    phone:       ['', [Validators.pattern(/^[+]?[0-9]{10,15}$/)]]
  });
  profileLoading = false;
  profileSaved   = false;
  profileError   = '';
  profileFormLoading = true;

  /* ── Formular schimbare parolă ── */
  passwordForm = this.fb.group({
    currentPassword: ['', [Validators.required]],
    newPassword:     ['', [Validators.required, Validators.minLength(8)]],
    confirmPassword: ['', [Validators.required]]
  }, { validators: this.passwordMatchValidator });
  passwordLoading = false;
  passwordSaved   = false;
  passwordError   = '';

  get f() { return this.profileForm.controls; }
  get p() { return this.passwordForm.controls; }

  get initials(): string {
    return this.username[0]?.toUpperCase() ?? '?';
  }

  ngOnInit(): void {
    /* Cerere 1: email + createdAt */
    this.service.getMe().subscribe({
      next: me => {
        this.email     = me.email;
        this.createdAt = me.createdAt;
        this.meLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.meLoading = false;
        this.cdr.detectChanges();
      }
    });

    /* Cerere 2: date personale — independentă */
    this.service.getOwnProfile().subscribe({
      next: profile => {
        this.profileFormLoading = false;
        if (profile) {
          this.profileForm.patchValue({
            firstName:   profile.firstName,
            lastName:    profile.lastName,
            cnp:         profile.cnp         ?? '',
            dateOfBirth: profile.dateOfBirth ?? '',
            phone:       profile.phone       ?? ''
          });
        }
        this.cdr.detectChanges();
      },
      error: () => {
        this.profileFormLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  submitProfile(): void {
    if (this.profileForm.invalid) { this.profileForm.markAllAsTouched(); return; }
    this.profileLoading = true;
    this.profileSaved   = false;
    this.profileError   = '';
    const v = this.profileForm.value;
    this.service.saveProfile({
      firstName:   v.firstName!,
      lastName:    v.lastName!,
      cnp:         v.cnp         || undefined,
      dateOfBirth: v.dateOfBirth || undefined,
      phone:       v.phone       || undefined
    }).subscribe({
      next: () => {
        this.profileSaved   = true;
        this.profileLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.profileError   = 'Eroare la salvarea datelor.';
        this.profileLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  submitPassword(): void {
    if (this.passwordForm.invalid) { this.passwordForm.markAllAsTouched(); return; }
    this.passwordLoading = true;
    this.passwordSaved   = false;
    this.passwordError   = '';
    this.service.changePassword({
      currentPassword: this.p['currentPassword'].value!,
      newPassword:     this.p['newPassword'].value!
    }).subscribe({
      next: () => {
        this.passwordSaved   = true;
        this.passwordLoading = false;
        this.passwordForm.reset();
        this.cdr.detectChanges();
      },
      error: (err) => {
        const msg = err?.error?.error;
        this.passwordError = msg === 'Parola curentă este incorectă'
          ? 'Parola curentă este incorectă.'
          : 'Eroare la schimbarea parolei.';
        this.passwordLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  private passwordMatchValidator(group: AbstractControl) {
    const pw   = group.get('newPassword')?.value;
    const conf = group.get('confirmPassword')?.value;
    return pw && conf && pw !== conf ? { passwordMismatch: true } : null;
  }
}
