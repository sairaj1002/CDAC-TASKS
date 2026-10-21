import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [RouterModule.forChild([{ path: '', pathMatch: 'full', redirectTo: '/wms' }])],
})
export class WmsDemoModule {}
