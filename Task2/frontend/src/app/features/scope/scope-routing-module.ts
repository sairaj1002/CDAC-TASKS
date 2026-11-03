import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  { path: 'road', loadChildren: () => import('./road/road-module').then(m => m.RoadModule) },
  { path: 'bridge', loadChildren: () => import('./bridge/bridge-module').then(m => m.BridgeModule) },
  { path: 'building', loadChildren: () => import('./building/building-module').then(m => m.BuildingModule) },
  { path: 'others', loadChildren: () => import('./others/others-module').then(m => m.OthersModule) },
  { path: 'dropdown-master', loadChildren: () => import('./dropdown-admin/dropdown-admin-module').then(m => m.DropdownAdminModule) },
  { path: '', redirectTo: 'road', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ScopeRoutingModule {}
