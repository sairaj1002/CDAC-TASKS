import { Component } from '@angular/core';

@Component({
  selector: 'app-wamis-footer',
  standalone: false,
  templateUrl: './footer.html',
})
export class WamisFooterComponent {
  copyright = '© 2026 Designed and Developed by C-DAC Pune';
  versionInfo = 'Version : 1.0.35+26.10.13 (Updated on 29.10.2026)';
}
