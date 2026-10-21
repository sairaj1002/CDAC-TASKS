import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { EmbComponent } from './pages/emb-dashboard/emb-dashboard.component';

const routes: Routes = [{ path: '', component: EmbComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class EmbRoutingModule {}
