import { Component, EventEmitter, Output } from '@angular/core';
import { APP_CONSTANTS } from '../../../common/constants/app.constants';

@Component({
  selector: 'app-wamis-header',
  standalone: false,
  templateUrl: './header.html',
})
export class WamisHeaderComponent {
  appName = APP_CONSTANTS.APP_NAME;
  appFullName = APP_CONSTANTS.APP_FULL_NAME;
  activeDivision = 'O/o CCE CRWC BBSR';
  showDivisionDropdown = false;

  divisions = [
    'O/o CCE CRWC BBSR',
    'Division Office Pune',
    'Division Office Mumbai',
    'Division Office Nagpur',
  ];

  @Output() toggleSidebar = new EventEmitter<void>();

  toggleDropdown(): void {
    this.showDivisionDropdown = !this.showDivisionDropdown;
  }

  selectDivision(div: string): void {
    this.activeDivision = div;
    this.showDivisionDropdown = false;
  }
}

