import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';

import { FooterComponent } from './footer/footer.component';
import { HeaderComponent } from './header/header.component';
import { MainLayoutComponent } from './main-layout/main-layout.component';
import { SidebarComponent } from './sidebar/sidebar.component';

@NgModule({
  declarations: [FooterComponent, HeaderComponent, MainLayoutComponent, SidebarComponent],
  imports: [RouterModule, MatButtonModule, MatIconModule, MatTooltipModule],
  exports: [MainLayoutComponent],
})
export class LayoutModule {}
