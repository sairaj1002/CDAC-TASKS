import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { WorkInspectionViewComponent } from './work-inspection-view/work-inspection-view.component';
import { AccessService, FUNCTION, OPERATION } from '../../common/services/access.service';

@Component({
  selector: 'app-work-inspection-outer',
  standalone: false,
  templateUrl: './work-inspection-outer.component.html',
})
export class WorkInspectionOuterComponent implements OnInit {

  @ViewChild(WorkInspectionViewComponent) viewComponent?: WorkInspectionViewComponent;

  // The tabs of the user, in this order; a tab the user may not use is not there at all (only View for a view-only user)
  tabs: string[] = [];
  selectedTabIndex = 0;

  constructor(private readonly router: Router, private readonly access: AccessService) { }

  get canAdd(): boolean {
    return this.access.can(FUNCTION.WORK_INSPECTION, OPERATION.ADD);
  }

  get canView(): boolean {
    return this.access.can(FUNCTION.WORK_INSPECTION, OPERATION.VIEW);
  }

  ngOnInit(): void {
    this.tabs = [...(this.canAdd ? [OPERATION.ADD] : []), ...(this.canView ? [OPERATION.VIEW] : [])];
    // If the URL ends with /view, select the View tab; otherwise the Add tab. Without the right on a tab the other one opens.
    const wantsView = this.router.url.includes('/view');
    const opening = (wantsView && this.canView) || !this.canAdd ? OPERATION.VIEW : OPERATION.ADD;
    this.selectedTabIndex = Math.max(this.tabs.indexOf(opening), 0);
    if (opening === OPERATION.VIEW) {
      setTimeout(() => this.viewComponent?.loadGradedWorks(), 0);
    }
  }

  onTabChange(event: MatTabChangeEvent): void {
    const isQmsPrefix = this.router.url.includes('/qms/');
    const basePath = isQmsPrefix ? '/home/qms/work-inspection' : '/home/work-inspection';
    const tab = this.tabs[event.index];

    if (tab === OPERATION.VIEW) {
      this.selectedTabIndex = event.index;
      this.router.navigate([`${basePath}/view`]);
      this.viewComponent?.loadGradedWorks();
    } else {
      this.selectedTabIndex = event.index;
      this.router.navigate([`${basePath}/add`]);
    }
  }
}
