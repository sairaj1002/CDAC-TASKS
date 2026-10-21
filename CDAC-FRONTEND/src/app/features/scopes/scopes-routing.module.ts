import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { ScopesDashboardComponent } from './pages/scopes-dashboard/scopes-dashboard.component';

const routes: Routes = [{ path: '', component: ScopesDashboardComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ScopesRoutingModule {}
