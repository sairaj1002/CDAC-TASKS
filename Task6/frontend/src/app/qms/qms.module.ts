import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatTabsModule } from '@angular/material/tabs';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialogModule } from '@angular/material/dialog';
import { TranslateModule } from '@ngx-translate/core';

import { QmsRoutingModule } from './qms-routing.module';
import { ItemComponent } from './item/item.component';
import { ItemAuditComponent } from './item/item-audit/item-audit.component';
import { WorkAssignmentOuterComponent } from './work-assignment/work-assignment-outer.component';
import { WorkAssignmentAddComponent } from './work-assignment/work-assignment-add/work-assignment-add.component';
import { WorkAssignmentViewComponent } from './work-assignment/work-assignment-view/work-assignment-view.component';
import { AllocationDetailsComponent } from './work-assignment/work-assignment-add/allocation-details.component';
import { WorkInspectionAndReportingComponent } from './work-inspection/work-inspection-and-reporting/work-inspection-and-reporting.component';
import { WorkInspectionOuterComponent } from './work-inspection/work-inspection-outer.component';
import { WorkInspectionViewComponent } from './work-inspection/work-inspection-view/work-inspection-view.component';

@NgModule({
  declarations: [
    ItemComponent,
    ItemAuditComponent,
    WorkAssignmentOuterComponent,
    WorkAssignmentAddComponent,
    WorkAssignmentViewComponent,
    AllocationDetailsComponent,
    WorkInspectionAndReportingComponent,
    WorkInspectionOuterComponent,
    WorkInspectionViewComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    QmsRoutingModule,
    MatTabsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatTableModule,
    MatIconModule,
    MatExpansionModule,
    MatPaginatorModule,
    MatSnackBarModule,
    MatDialogModule,
    TranslateModule,
  ],
})
export class QmsModule {}