import { Component } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-coverage',
  standalone: true,
  templateUrl: './coverage.html',
  styleUrl: './coverage.scss'
})
export class CoverageComponent {
  coverageUrl: SafeResourceUrl;

  constructor(sanitizer: DomSanitizer) {
    this.coverageUrl = sanitizer.bypassSecurityTrustResourceUrl(
      `${environment.apiUrl}/coverage/index.html`
    );
  }
}
