import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './home.component';
import { WamisMasterComponent } from '../components/master/wamis-master';

const routes: Routes = [{
  path: 'home',
  component: HomeComponent,
  children: [
    // WMS > Tender > Pre-Tender; one route for the Add and View tabs, so the page (and a record being edited)
    // stays when the tab changes
    { path: 'wms/pre-tender', redirectTo: 'wms/pre-tender/add', pathMatch: 'full' },
    { path: 'wms/pre-tender/:tab', component: WamisMasterComponent },
    {
      path: 'qms',
      loadChildren: () => import('../qms/qms.module').then(m => m.QmsModule)
    }
  ]
}];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class HomeRoutingModule { }
