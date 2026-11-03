import { NgModule, provideBrowserGlobalErrorListeners } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { provideHttpClient, withInterceptorsFromDi, HTTP_INTERCEPTORS } from '@angular/common/http';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatMenuModule } from '@angular/material/menu';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTabsModule } from '@angular/material/tabs';

import { AppRoutingModule } from './app-routing-module';
import { App } from './app';

// Scope Feature Module
import { ScopeModule } from './features/scope/scope-module';

// Core
import { ApiErrorInterceptor } from './core/interceptors/api-error.interceptor';

// Layout
import { HeaderComponent } from './layout/header/header.component';
import { SidebarComponent } from './layout/sidebar/sidebar.component';
import { MainLayoutComponent } from './layout/main-layout/main-layout.component';

// Shared Components
import { ConfirmDialogComponent } from './shared/components/confirm-dialog/confirm-dialog.component';
import { DetailsDialogComponent } from './shared/components/details-dialog/details-dialog.component';
import { AuditDialogComponent } from './shared/components/audit-dialog/audit-dialog.component';
import { JsonViewerComponent } from './shared/components/json-viewer/json-viewer.component';
import { StatusChipComponent } from './shared/components/status-chip/status-chip.component';
import { CustomSelectComponent } from './shared/components/custom-select/custom-select.component';
import { EmptyTabComponent } from './shared/components/empty-tab/empty-tab.component';
import { FieldTooltipDirective } from './shared/directives/field-tooltip.directive';

// Feature Components
import { SubWorkDetailsComponent } from './features/gepnic/components/sub-work-details/sub-work-details.component';
import { BoqDetailsComponent } from './features/gepnic/components/boq-details/boq-details.component';
import { PretenderFormComponent } from './features/gepnic/components/pretender-form/pretender-form.component';
import { RequestResponseViewerComponent } from './features/gepnic/components/request-response-viewer/request-response-viewer.component';

// Feature Pages
import { TenderPageComponent } from './features/gepnic/pages/tender-page/tender-page.component';
import { TenderPushComponent } from './features/gepnic/pages/tender-push/tender-push.component';
import { TenderCorrigendumPullComponent } from './features/gepnic/pages/tender-corrigendum-pull/tender-corrigendum-pull.component';
import { AocManagementComponent } from './features/gepnic/pages/aoc-management/aoc-management.component';

@NgModule({
  declarations: [
    App,
    // Layout
    HeaderComponent,
    SidebarComponent,
    MainLayoutComponent,
    // Shared
    ConfirmDialogComponent,
    DetailsDialogComponent,
    AuditDialogComponent,
    JsonViewerComponent,
    StatusChipComponent,
    CustomSelectComponent,
    EmptyTabComponent,
    FieldTooltipDirective,
    // Feature components
    SubWorkDetailsComponent,
    BoqDetailsComponent,
    PretenderFormComponent,
    RequestResponseViewerComponent,
    // Feature pages
    TenderPageComponent,
    TenderPushComponent,
    TenderCorrigendumPullComponent,
    AocManagementComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    AppRoutingModule,
    ScopeModule,
    MatDialogModule,
    MatSnackBarModule,
    MatTooltipModule,
    MatIconModule,
    MatTableModule,
    MatSelectModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatCardModule,
    MatMenuModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatTabsModule
  ],
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideHttpClient(withInterceptorsFromDi()),
    {
      provide: HTTP_INTERCEPTORS,
      useClass: ApiErrorInterceptor,
      multi: true
    }
  ],
  bootstrap: [App]
})
export class AppModule { }
