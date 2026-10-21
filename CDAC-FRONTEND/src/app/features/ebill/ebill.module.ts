import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';
import { EbillRoutingModule } from './ebill-routing.module';
import { EbillComponent } from './pages/ebill-dashboard/ebill-dashboard.component';

@NgModule({
  declarations: [EbillComponent],
  imports: [SharedModule, EbillRoutingModule],
})
export class EbillModule {}
