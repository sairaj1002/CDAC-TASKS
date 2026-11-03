import { Component, EventEmitter, Input, Output } from '@angular/core';
import { PreTenderResponseForm } from '../../common/models/app.models';

@Component({
  selector: 'app-wamis-master',
  standalone: false,
  templateUrl: './wamis-master.html',
})
export class WamisMasterComponent {
  @Input() activeTabIndex = 0;
  @Output() tabChanged = new EventEmitter<number>();

  selectedTenderForEdit: PreTenderResponseForm | null = null;

  tabs = [
    { id: 0, label: 'List of Works', icon: 'business_center' },
    { id: 1, label: 'Pre-Tender Entry Form', icon: 'description' },
    { id: 2, label: 'Master Dropdowns', icon: 'tune' },
    { id: 3, label: 'Integration & Audit Trail', icon: 'history' },
  ];

  selectTab(index: number): void {
    this.activeTabIndex = index;
    if (index !== 1) {
      this.selectedTenderForEdit = null;
    }
    this.tabChanged.emit(index);
  }

  onNavigateToForm(): void {
    this.selectedTenderForEdit = null;
    this.selectTab(1);
  }

  onEditPreTender(tender: PreTenderResponseForm): void {
    this.selectedTenderForEdit = tender;
    this.selectTab(1);
  }

  onFormSubmitted(): void {
    this.selectedTenderForEdit = null;
    this.selectTab(0);
  }

  onFormCancelled(): void {
    this.selectedTenderForEdit = null;
    this.selectTab(0);
  }
}

