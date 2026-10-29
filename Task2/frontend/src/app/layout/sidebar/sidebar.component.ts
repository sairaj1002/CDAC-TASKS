import { Component } from '@angular/core';
import { GEPNIC_CONSTANTS } from '../../features/gepnic/constants/gepnic.constants';

export interface WamisRailModule {
  id: string;
  name: string;
  icon: string;
  active?: boolean;
}

export interface SubNavItem {
  label: string;
  route: string;
  icon: string;
}

@Component({
  selector: 'app-sidebar',
  standalone: false,
  templateUrl: './sidebar.component.html'
})
export class SidebarComponent {

  isExpanded = true;
  selectedModuleId = 'Integration';

  // Primary WAMIS Vertical Modules Rail: Strictly WMS and Integration (AMS removed)
  railModules: WamisRailModule[] = [...GEPNIC_CONSTANTS.RAIL_MODULES];

  // Accordion expansion states
  isGepnicExpanded = true;
  isWorksExpanded = true;

  // 1. Integration Menu -> Heading: "Gepnic"
  gepnicNavItems: SubNavItem[] = [...GEPNIC_CONSTANTS.GEPNIC_NAV_ITEMS];

  // 2. WMS Menu -> Heading: "Works"
  wmsNavItems: SubNavItem[] = [...GEPNIC_CONSTANTS.WMS_NAV_ITEMS];

  toggleGepnic(): void {
    this.isGepnicExpanded = !this.isGepnicExpanded;
  }

  toggleWorks(): void {
    this.isWorksExpanded = !this.isWorksExpanded;
  }

  toggle(): void {
    this.isExpanded = !this.isExpanded;
  }

  closeDrawer(): void {
    this.isExpanded = false;
  }

  openDrawer(): void {
    this.isExpanded = true;
  }

  selectModule(moduleId: string): void {
    if (this.selectedModuleId === moduleId) {
      this.isExpanded = !this.isExpanded;
      return;
    }

    this.selectedModuleId = moduleId;
    this.railModules.forEach(m => m.active = (m.id === moduleId));
    this.isExpanded = true;
  }
}
