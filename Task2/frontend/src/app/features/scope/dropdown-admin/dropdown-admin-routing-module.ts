import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DropdownAdminComponent } from './components/dropdown-admin.component';

const routes: Routes = [
  { path: '', component: DropdownAdminComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DropdownAdminRoutingModule {}
