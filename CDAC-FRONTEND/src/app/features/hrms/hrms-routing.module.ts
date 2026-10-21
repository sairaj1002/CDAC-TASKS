import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { HrmsComponent } from './pages/hrms-dashboard/hrms-dashboard.component';

const routes: Routes = [{ path: '', component: HrmsComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class HrmsRoutingModule {}
