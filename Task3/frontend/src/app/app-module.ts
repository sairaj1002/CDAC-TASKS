import { NgModule, provideBrowserGlobalErrorListeners } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';

// Angular Material Modules
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatTabsModule } from '@angular/material/tabs';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatRadioModule } from '@angular/material/radio';

// App Routing & Root Component
import { AppRoutingModule } from './app-routing-module';
import { App } from './app';

// Layout Components
import { WamisHeaderComponent } from './components/layout/header/header';
import { WamisSidebarComponent } from './components/layout/sidebar/sidebar';
import { WamisFooterComponent } from './components/layout/footer/footer';

// Master Workspace Component
import { WamisMasterComponent } from './components/master/wamis-master';

// Child Components
import { WorkListChildComponent } from './components/child/work-list/work-list';
import { PreTenderFormChildComponent } from './components/child/pretender-form/pretender-form';
import { GepnicConfigComponent } from './components/child/gepnic-config/gepnic-config';
import { GepnicTenderConfigComponent } from './components/child/gepnic-tender-config/gepnic-tender-config';

// Shared Dialog & Feedback Components
import { ConfirmDialogComponent } from './components/shared/confirm-dialog/confirm-dialog';
import { ViewDetailsDialogComponent } from './components/shared/view-details-dialog/view-details-dialog';
import { AuditDetailsDialogComponent } from './components/shared/audit-details-dialog/audit-details-dialog';
import { UploadBoqDialogComponent } from './components/shared/upload-boq-dialog/upload-boq-dialog';
import { CustomSnackbarComponent } from './components/shared/custom-snackbar/custom-snackbar';

@NgModule({
  declarations: [
    App,
    // Layout
    WamisHeaderComponent,
    WamisSidebarComponent,
    WamisFooterComponent,
    // Master
    WamisMasterComponent,
    // Children
    WorkListChildComponent,
    PreTenderFormChildComponent,
    GepnicConfigComponent,
    GepnicTenderConfigComponent,
    // Shared
    ConfirmDialogComponent,
    ViewDetailsDialogComponent,
    AuditDetailsDialogComponent,
    UploadBoqDialogComponent,
    CustomSnackbarComponent,
  ],
  imports: [
    BrowserModule,
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    AppRoutingModule,
    // Material
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    MatPaginatorModule,
    MatDialogModule,
    MatSnackBarModule,
    MatTooltipModule,
    MatTabsModule,
    MatCheckboxModule,
    MatRadioModule,
  ],
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideHttpClient(withInterceptorsFromDi()),
    provideAnimationsAsync(),
  ],
  bootstrap: [App],
})
export class AppModule {}
