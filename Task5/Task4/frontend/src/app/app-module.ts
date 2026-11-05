import { ErrorHandler, NgModule, provideBrowserGlobalErrorListeners, provideZoneChangeDetection } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptors, withInterceptorsFromDi } from '@angular/common/http';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { TranslateModule } from '@ngx-translate/core';

// Angular Material Modules
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatSortModule } from '@angular/material/sort';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatTabsModule } from '@angular/material/tabs';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatTimepickerModule } from '@angular/material/timepicker';
import { MAT_DATE_LOCALE, provideNativeDateAdapter } from '@angular/material/core';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatRadioModule } from '@angular/material/radio';

// App Routing, Root Component and the WAMIS shell (header, module rail, menu, footer) from QMS
import { AppRoutingModule } from './app-routing-module';
import { App } from './app';
import { HomeModule } from './home/home.module';

// WMS Pre-Tender page
import { WamisMasterComponent } from './components/master/wamis-master';
import { WorkListChildComponent } from './components/child/work-list/work-list';
import { PreTenderFormChildComponent } from './components/child/pretender-form/pretender-form';

// WMS Post-Tender page and dialogs
import { PostTenderMasterComponent } from './components/post-tender/post-tender-master';
import { PostTenderViewComponent } from './components/post-tender/post-tender-view/post-tender-view';
import { PostTenderCorrigendumComponent } from './components/post-tender/post-tender-corrigendum/post-tender-corrigendum';
import { PostTenderNegotiationComponent } from './components/post-tender/post-tender-negotiation/post-tender-negotiation';
import { AocDetailsDialogComponent } from './components/post-tender/aoc-details-dialog/aoc-details-dialog';

// Shared Dialog & Feedback Components
import { DialogComponent } from './common/dialog/dialog.component';
import { ConfirmDialogComponent } from './components/shared/confirm-dialog/confirm-dialog';
import { ViewDetailsDialogComponent } from './components/shared/view-details-dialog/view-details-dialog';
import { AuditDetailsDialogComponent } from './components/shared/audit-details-dialog/audit-details-dialog';
import { UploadBoqDialogComponent } from './components/shared/upload-boq-dialog/upload-boq-dialog';
import { CustomSnackbarComponent } from './components/shared/custom-snackbar/custom-snackbar';

// HTTP interceptors and error handling
import { LoadingInterceptor } from './common/interceptors/loading.interceptor';
import { exceptionInterceptor } from './common/security/http-interceptors/exception-interceptor';
import { userIdInterceptor } from './common/security/http-interceptors/user-id-interceptor';
import { GlobalErrorHandler } from './common/error-handler/global-error-handler';

@NgModule({
  declarations: [
    App,
    // WMS Pre-Tender
    WamisMasterComponent,
    WorkListChildComponent,
    PreTenderFormChildComponent,
    // WMS Post-Tender
    PostTenderMasterComponent,
    PostTenderViewComponent,
    PostTenderCorrigendumComponent,
    PostTenderNegotiationComponent,
    AocDetailsDialogComponent,
    // Shared
    DialogComponent,
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
    HomeModule,
    AppRoutingModule,
    TranslateModule.forRoot(),
    // Material
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatTableModule,
    MatSortModule,
    MatPaginatorModule,
    MatDialogModule,
    MatSnackBarModule,
    MatTooltipModule,
    MatTabsModule,
    MatExpansionModule,
    MatAutocompleteModule,
    MatDatepickerModule,
    MatTimepickerModule,
    MatCheckboxModule,
    MatRadioModule,
  ],
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideHttpClient(withInterceptors([userIdInterceptor, exceptionInterceptor]), withInterceptorsFromDi()),
    { provide: HTTP_INTERCEPTORS, useClass: LoadingInterceptor, multi: true },
    { provide: ErrorHandler, useClass: GlobalErrorHandler },
    // dates as dd/mm/yyyy and times as 24-hour hh:mm in the date and time pickers
    provideNativeDateAdapter(),
    { provide: MAT_DATE_LOCALE, useValue: 'en-GB' },
    provideAnimationsAsync(),
  ],
  bootstrap: [App],
})
export class AppModule {}
