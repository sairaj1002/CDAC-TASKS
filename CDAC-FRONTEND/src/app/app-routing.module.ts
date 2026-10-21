import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { MainLayoutComponent } from './layout/main-layout/main-layout.component';

const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      { path: '', redirectTo: 'wms', pathMatch: 'full' },
      {
        path: 'wms',
        loadChildren: () => import('./features/wms/wms.module').then((module) => module.WmsModule),
      },
      {
        path: 'scopes',
        loadChildren: () =>
          import('./features/scopes/scopes.module').then((module) => module.ScopesModule),
      },
      {
        path: 'wmsdemo',
        loadChildren: () =>
          import('./features/wms/wms-demo.module').then((module) => module.WmsDemoModule),
      },
      {
        path: 'mis',
        loadChildren: () => import('./features/mis/mis.module').then((module) => module.MisModule),
      },
      {
        path: 'ams',
        loadChildren: () => import('./features/ams/ams.module').then((module) => module.AmsModule),
      },
      {
        path: 'ebill',
        loadChildren: () =>
          import('./features/ebill/ebill.module').then((module) => module.EbillModule),
      },
      {
        path: 'emb',
        loadChildren: () => import('./features/emb/emb.module').then((module) => module.EmbModule),
      },
      {
        path: 'hrms',
        loadChildren: () =>
          import('./features/hrms/hrms.module').then((module) => module.HrmsModule),
      },
      {
        path: 'ums',
        loadChildren: () => import('./features/ums/ums.module').then((module) => module.UmsModule),
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { useHash: true })], // <--- Write it here
  exports: [RouterModule],
})
export class AppRoutingModule {}
