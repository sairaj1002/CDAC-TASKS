import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-wamis-header',
  standalone: false,
  templateUrl: './header.html',
})
export class WamisHeaderComponent {
  appFullName = 'WORKS & ACCOUNTS MANAGEMENT SYSTEM , CENTRE FOR DEVELOPMENT OF ADVANCED COMPUTING';
  activeDivision = 'TEST DIVISION OFFICE';
  showDivisionDropdown = false;
  avatarText = 'VKJ';

  divisions = [
    'TEST DIVISION OFFICE',
    'O/o CCE CRWC BBSR',
    'Division Office Pune',
    'Division Office Mumbai',
  ];

  @Output() toggleSidebar = new EventEmitter<void>();

  toggleDropdown(): void {
    this.showDivisionDropdown = !this.showDivisionDropdown;
  }

  selectDivision(div: string): void {
    this.activeDivision = div;
    this.showDivisionDropdown = false;
  }

  clearDivision(event: MouseEvent): void {
    event.stopPropagation();
    this.activeDivision = 'TEST DIVISION OFFICE';
  }
}
