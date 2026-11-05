import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ItemComponent } from './item/item.component';
import { WorkAssignmentOuterComponent } from './work-assignment/work-assignment-outer.component';
import { WorkInspectionOuterComponent } from './work-inspection/work-inspection-outer.component';
import { accessGuard } from '../common/security/access.guard';
import { FUNCTION, OPERATION } from '../common/services/access.service';

// Loaded under /home/qms
const routes: Routes = [
  { path: 'item', component: ItemComponent, canActivate: [accessGuard], data: { functionCode: FUNCTION.ITEM } },
  { path: 'work-assignment', component: WorkAssignmentOuterComponent, canActivate: [accessGuard], data: { functionCode: FUNCTION.WORK_ASSIGNMENT } },
  { path: 'work-assignment/add', component: WorkAssignmentOuterComponent, canActivate: [accessGuard], data: { functionCode: FUNCTION.WORK_ASSIGNMENT, operation: OPERATION.ADD } },
  { path: 'work-assignment/view', component: WorkAssignmentOuterComponent, canActivate: [accessGuard], data: { functionCode: FUNCTION.WORK_ASSIGNMENT, operation: OPERATION.VIEW } },
  { path: 'work-inspection', component: WorkInspectionOuterComponent, canActivate: [accessGuard], data: { functionCode: FUNCTION.WORK_INSPECTION } },
  { path: 'work-inspection/add', component: WorkInspectionOuterComponent, canActivate: [accessGuard], data: { functionCode: FUNCTION.WORK_INSPECTION, operation: OPERATION.ADD } },
  { path: 'work-inspection/view', component: WorkInspectionOuterComponent, canActivate: [accessGuard], data: { functionCode: FUNCTION.WORK_INSPECTION, operation: OPERATION.VIEW } },
  { path: 'work-inspection/work-inspection-and-reporting', redirectTo: 'work-inspection/add', pathMatch: 'full' },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class QmsRoutingModule {}