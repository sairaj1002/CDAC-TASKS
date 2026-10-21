import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-header',
  standalone: false,
  template: `
    <header class="app-header">
      <div class="app-name">WAMIS:Works & Accounts Management Information System</div>
    </header>

    <header class="topbar">
      <button
        mat-icon-button
        type="button"
        class="menu-btn"
        aria-label="Expand or collapse navigation"
        [attr.aria-expanded]="expanded()"
        (click)="toggleNavigation.emit()"
      >
        <mat-icon>{{ expanded() ? 'menu_open' : 'menu' }}</mat-icon>
      </button>

      <a routerLink="" class="brand">Home</a>| <a routerLink="/about" class="brand">AboutUs</a>|
      <a routerLink="/contact" class="brand">ContactUs</a>
    </header>
  `,
  styleUrl: './header.component.css',
})
export class HeaderComponent {
  readonly expanded = input(false);
  readonly toggleNavigation = output<void>();
}
