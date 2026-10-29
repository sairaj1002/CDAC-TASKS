import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MainLayoutComponent } from './layout/main-layout/main-layout.component';
import { GEPNIC_ROUTES } from './features/gepnic/gepnic.routes';

const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    children: GEPNIC_ROUTES
  },
  { path: '**', redirectTo: '' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
