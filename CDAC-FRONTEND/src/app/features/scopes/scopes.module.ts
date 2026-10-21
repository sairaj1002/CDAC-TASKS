import { NgModule } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBarModule } from '@angular/material/snack-bar';

import { SharedModule } from '../../shared/shared.module';
import { ScopesDashboardComponent } from './pages/scopes-dashboard/scopes-dashboard.component';
import { ScopesRoutingModule } from './scopes-routing.module';

@NgModule({
  declarations: [ScopesDashboardComponent],
  imports: [
    SharedModule,
    ScopesRoutingModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatSelectModule,
    MatSnackBarModule,
  ],
})
export class ScopesModule {}
