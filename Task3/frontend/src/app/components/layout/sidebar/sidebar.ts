import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-wamis-sidebar',
  standalone: false,
  templateUrl: './sidebar.html',
})
export class WamisSidebarComponent {
  @Input() activeTab = 0;
  @Output() tabSelected = new EventEmitter<number>();

  navItems = [
    { id: 0, label: 'Works', icon: 'business_center' },
    { id: 1, label: 'Pre-Tender', icon: 'description' },
    { id: 2, label: 'Masters', icon: 'tune' },
    { id: 3, label: 'Audit Trail', icon: 'history' },
  ];

  selectNav(id: number): void {
    this.tabSelected.emit(id);
  }
}

