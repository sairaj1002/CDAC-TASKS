import { Component, input } from '@angular/core';

interface NavItem {
  label: string;
  route: string;
  icon: string;
}

@Component({
  selector: 'app-sidebar',
  standalone: false,
  host: {
    '[class.expanded]': 'expanded()',
  },
  template: `
    <nav class="drawer" aria-label="Modules">
      @for (item of items; track item.route) {
        <a
          class="drawer-item"
          [routerLink]="item.route"
          routerLinkActive="active"
          [matTooltip]="item.label"
          matTooltipPosition="right"
          [matTooltipDisabled]="expanded()"
        >
          <mat-icon class="drawer-icon">{{ item.icon }}</mat-icon>
          <span class="label">{{ item.label }}</span>
        </a>
        <hr class="drawer-divider" />
      }
    </nav>
  `,
  styleUrl: './sidebar.component.css',
})
export class SidebarComponent {
  readonly expanded = input(false);

  readonly items: NavItem[] = [
    { label: 'WMS', route: '/wms', icon: 'warehouse' },
    { label: 'Scopes', route: '/scopes', icon: 'construction' },
    { label: 'MIS', route: '/mis', icon: 'insights' },
    { label: 'AMS', route: '/ams', icon: 'inventory_2' },
    { label: 'EBILL', route: '/ebill', icon: 'receipt_long' },
    { label: 'EMB', route: '/emb', icon: 'menu_book' },
    { label: 'HRMS', route: '/hrms', icon: 'groups' },
    { label: 'UMS', route: '/ums', icon: 'manage_accounts' },
  ];
}
