import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { RoadModule } from './road/road-module';
import { BridgeModule } from './bridge/bridge-module';
import { BuildingModule } from './building/building-module';
import { OthersModule } from './others/others-module';
import { DropdownAdminModule } from './dropdown-admin/dropdown-admin-module';

@NgModule({
  imports: [
    CommonModule,
    RoadModule,
    BridgeModule,
    BuildingModule,
    OthersModule,
    DropdownAdminModule,
  ],
  exports: [
    RoadModule,
    BridgeModule,
    BuildingModule,
    OthersModule,
    DropdownAdminModule,
  ],
})
export class ScopeModule {}
