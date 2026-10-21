import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { EbillComponent } from './pages/ebill-dashboard/ebill-dashboard.component';

const routes: Routes = [{ path: '', component: EbillComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class EbillRoutingModule {}
