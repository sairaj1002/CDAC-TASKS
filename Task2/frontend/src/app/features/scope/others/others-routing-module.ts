import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Others } from './components/others';

const routes: Routes = [
  { path: '', component: Others }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class OthersRoutingModule {}
