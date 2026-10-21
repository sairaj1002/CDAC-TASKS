import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UmsComponent } from './pages/ums-dashboard/ums-dashboard.component';

const routes: Routes = [{ path: '', component: UmsComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class UmsRoutingModule {}
