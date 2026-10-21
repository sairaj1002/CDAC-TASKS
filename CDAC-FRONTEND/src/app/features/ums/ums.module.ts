import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';
import { UmsRoutingModule } from './ums-routing.module';
import { UmsComponent } from './pages/ums-dashboard/ums-dashboard.component';

@NgModule({
  declarations: [UmsComponent],
  imports: [SharedModule, UmsRoutingModule],
})
export class UmsModule {}
