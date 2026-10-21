import { NgModule } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatNativeDateModule } from '@angular/material/core';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { MatTabsModule } from '@angular/material/tabs';

import { SharedModule } from '../../shared/shared.module';
import { WmsRoutingModule } from './wms-routing.module';
import { AuditHistoryDialogComponent } from './pages/wms-dashboard/work-audit-dialog.component';
import { WorkReportDetailsDialogComponent } from './pages/wms-dashboard/work-report-details-dialog.component';
import { WmsDashboardComponent } from './pages/wms-dashboard/wms-dashboard.component';

@NgModule({
  declarations: [
    AuditHistoryDialogComponent,
    WorkReportDetailsDialogComponent,
    WmsDashboardComponent,
  ],
  imports: [
    SharedModule,
    WmsRoutingModule,
    MatButtonModule,
    MatCardModule,
    MatDatepickerModule,
    MatDialogModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatNativeDateModule,
    MatPaginatorModule,
    MatSelectModule,
    MatSnackBarModule,
    MatTableModule,
    MatTabsModule,
  ],
})
export class WmsModule {}
