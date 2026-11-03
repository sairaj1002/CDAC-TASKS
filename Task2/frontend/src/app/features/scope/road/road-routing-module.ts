import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RoadComponent } from './components/road-component';

const routes: Routes = [
  { path: '', component: RoadComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class RoadRoutingModule {}
