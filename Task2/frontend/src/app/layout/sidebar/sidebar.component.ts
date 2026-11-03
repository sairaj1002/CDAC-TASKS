import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs';
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
export class SidebarComponent implements OnInit {

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

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.syncActiveModule(this.router.url);
    this.router.events.pipe(
      filter((e): e is NavigationEnd => e instanceof NavigationEnd)
    ).subscribe(event => {
      this.syncActiveModule(event.urlAfterRedirects || event.url);
    });
  }

  private syncActiveModule(url: string): void {
    if (url.includes('/sub-work') || url.includes('/tender')) {
      this.selectedModuleId = 'WMS';
      this.railModules.forEach(m => m.active = (m.id === 'WMS'));
      this.isWorksExpanded = true;
    } else if (url.includes('/tender-push') || url.includes('/tender-corrigendum-pull') || url.includes('/awarding')) {
      this.selectedModuleId = 'Integration';
      this.railModules.forEach(m => m.active = (m.id === 'Integration'));
      this.isGepnicExpanded = true;
    }
  }

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
