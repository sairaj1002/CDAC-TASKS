import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';
import { MisRoutingModule } from './mis-routing.module';
import { MisComponent } from './pages/mis-dashboard/mis-dashboard.component';

@NgModule({
  declarations: [MisComponent],
  imports: [SharedModule, MisRoutingModule],
})
export class MisModule {}
