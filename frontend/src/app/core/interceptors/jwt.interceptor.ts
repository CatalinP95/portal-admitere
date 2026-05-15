import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  const token = auth.getToken();
  const authReq = token
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(authReq).pipe(
    catchError((err: HttpErrorResponse) => {
      // Exclude auth endpoints — 401 pe login/register e așteptat
      const isAuthEndpoint = req.url.includes('/auth/');
      if (err.status === 401 && !isAuthEndpoint) {
        // Curăță tokenurile local fără request suplimentar
        auth.clearTokens();
        router.navigate(['/login']);
      }
      return throwError(() => err);
    })
  );
};
