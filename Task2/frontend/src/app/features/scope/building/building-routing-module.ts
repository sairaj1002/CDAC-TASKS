import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Building } from './components/building';

const routes: Routes = [
  { path: '', component: Building }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class BuildingRoutingModule {}
