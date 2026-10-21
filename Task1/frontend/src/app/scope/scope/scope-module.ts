import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BridgeAdd } from '../bridge/bridge-add';
import { BridgeView } from '../bridge/bridge-view';
import { BridgeAuditDialog } from '../bridge/bridge-audit-dialog';
import { BridgeDetailsDialog } from '../bridge/bridge-details-dialog';
import { BridgeReadonlyField } from '../bridge/bridge-readonly-field';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { MatTabsModule } from '@angular/material/tabs';
import { ReactiveFormsModule } from '@angular/forms';
import { ScopeMaster } from './ScopeMaster';

@NgModule({
  declarations: [
    ScopeMaster,
    BridgeAdd,
    BridgeView,
    BridgeAuditDialog,
    BridgeDetailsDialog,
    BridgeReadonlyField,
  ],
  imports: [
    CommonModule,
    MatButtonModule,
    MatCardModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatSelectModule,
    MatSnackBarModule,
    MatTableModule,
    MatTabsModule,
    ReactiveFormsModule,
  ],
  exports: [ScopeMaster],
})
export class ScopeModule {}
