import { Component, EventEmitter, Input, Output } from '@angular/core';

export interface WamisRailItem {
  id: string;
  label: string;
  icon: string;
}

@Component({
  selector: 'app-wamis-sidebar',
  standalone: false,
  templateUrl: './sidebar.html',
})
export class WamisSidebarComponent {
  @Input() activeRailId = 'wms';
  @Input() activeSubMenu = 'pretender';
  @Output() subMenuSelected = new EventEmitter<string>();

  // Deep Blue Rail Items matching the screenshot
  railItems: WamisRailItem[] = [
    { id: 'profile', label: 'Profile', icon: 'folder' },
    { id: 'master', label: 'Master', icon: 'folder' },
    { id: 'hrms', label: 'HRMS', icon: 'folder' },
    { id: 'wms', label: 'WMS', icon: 'folder' }
  ];

  // Accordion states for the WMS second menu
  isSecondMenuOpen = true;
  expandedSections: { [key: string]: boolean } = {
    master: true,
    works: true,
    tenderDetails: true,
  };

  selectRail(item: WamisRailItem): void {
    this.activeRailId = item.id;
    if (item.id === 'wms' || item.id === 'master') {
      this.isSecondMenuOpen = true;
      if (item.id === 'master') {
        this.selectSubMenu('gepnic-config');
      }
    }
  }

  toggleSecondMenu(): void {
    this.isSecondMenuOpen = !this.isSecondMenuOpen;
  }

  toggleSection(section: string): void {
    this.expandedSections[section] = !this.expandedSections[section];
  }

  selectSubMenu(subId: string): void {
    this.activeSubMenu = subId;
    this.subMenuSelected.emit(subId);
  }
}
