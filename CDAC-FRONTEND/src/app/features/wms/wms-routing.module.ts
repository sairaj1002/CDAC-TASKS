import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { WmsDashboardComponent } from './pages/wms-dashboard/wms-dashboard.component';

const routes: Routes = [{ path: '', component: WmsDashboardComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class WmsRoutingModule {}
