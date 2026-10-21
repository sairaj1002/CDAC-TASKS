import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';
import { EmbRoutingModule } from './emb-routing.module';
import { EmbComponent } from './pages/emb-dashboard/emb-dashboard.component';

@NgModule({
  declarations: [EmbComponent],
  imports: [SharedModule, EmbRoutingModule],
})
export class EmbModule {}
