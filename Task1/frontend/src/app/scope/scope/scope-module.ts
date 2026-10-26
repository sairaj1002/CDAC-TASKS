import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTooltipModule } from '@angular/material/tooltip';

import { RoadAndBridgeComponent, ScopeMaster } from './ScopeMaster';
import { BridgeComponent } from '../bridge/bridge-component';
import { RoadComponent } from '../road/road-component';
import { DropdownAdminComponent } from '../dropdown-admin/dropdown-admin.component';

@NgModule({
  declarations: [
    RoadAndBridgeComponent,
    BridgeComponent,
    RoadComponent,
    DropdownAdminComponent,
  ],
  imports: [
    CommonModule,
    MatButtonModule,
    MatCardModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatMenuModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatSelectModule,
    MatSnackBarModule,
    MatTableModule,
    MatTabsModule,
    MatTooltipModule,
    FormsModule,
    ReactiveFormsModule,
  ],
  exports: [RoadAndBridgeComponent, ScopeMaster, BridgeComponent, RoadComponent, DropdownAdminComponent],
})
export class ScopeModule {}
