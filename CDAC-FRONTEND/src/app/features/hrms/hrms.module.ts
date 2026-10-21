import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';
import { HrmsRoutingModule } from './hrms-routing.module';
import { HrmsComponent } from './pages/hrms-dashboard/hrms-dashboard.component';

@NgModule({
  declarations: [HrmsComponent],
  imports: [SharedModule, HrmsRoutingModule],
})
export class HrmsModule {}
