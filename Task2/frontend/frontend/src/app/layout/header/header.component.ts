import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-header',
  standalone: false,
  templateUrl: './header.component.html'
})
export class HeaderComponent {
  @Output() menuToggle = new EventEmitter<void>();

  systemTitle = 'WORKS & ACCOUNTS MANAGEMENT SYSTEM';
  subTitle = 'CENTRE FOR DEVELOPMENT OF ADVANCED COMPUTING';
  environment = 'DEMOEPROC';

  divisionOffices: string[] = [
    'TEST DIVISION OFFICE',
    'TEST SUB DIVISION OFFICE',
    'TEST SECTION OFFICE'
  ];

  selectedOffice: string = 'TEST DIVISION OFFICE';
  isOfficeDropdownOpen = false;

  toggleOfficeDropdown(): void {
    this.isOfficeDropdownOpen = !this.isOfficeDropdownOpen;
  }

  selectOffice(office: string): void {
    this.selectedOffice = office;
    this.isOfficeDropdownOpen = false;
  }

  clearOffice(event: MouseEvent): void {
    event.stopPropagation();
    this.selectedOffice = '';
  }

  toggleSidebar(): void {
    this.menuToggle.emit();
  }
}
