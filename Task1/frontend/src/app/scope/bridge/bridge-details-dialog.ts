import { Component, inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BridgeComponent } from './bridge-component';

interface BridgeDetailsDialogData {
  component: BridgeComponent;
  serialNumber: number;
}

@Component({
  selector: 'app-bridge-details-dialog',
  standalone: false,
  templateUrl: './bridge-details-dialog.html',
})
export class BridgeDetailsDialog {
  readonly data = inject<BridgeDetailsDialogData>(MAT_DIALOG_DATA);

  get component(): BridgeComponent {
    return this.data.component;
  }
}
