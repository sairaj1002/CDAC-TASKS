import { Routes } from '@angular/router';
import { SubWorkDetailsComponent } from './components/sub-work-details/sub-work-details.component';
import { PretenderPageComponent } from './pages/pretender-page/pretender-page.component';
import { TenderPushComponent } from './pages/tender-push/tender-push.component';
import { DocumentManagementComponent } from './pages/document-management/document-management.component';
import { TenderCorrigendumPullComponent } from './pages/tender-corrigendum-pull/tender-corrigendum-pull.component';
import { AocManagementComponent } from './pages/aoc-management/aoc-management.component';
import { SystemDiagnosticsComponent } from './pages/system-diagnostics/system-diagnostics.component';

export const GEPNIC_ROUTES: Routes = [
  { path: '', redirectTo: 'sub-work', pathMatch: 'full' },
  { path: 'sub-work', component: SubWorkDetailsComponent },
  { path: 'pretender', redirectTo: 'sub-work' },
  { path: 'pre-tender', redirectTo: 'pretender' },
  { path: 'pre-tender-configuration', redirectTo: 'pretender' },
  { path: 'dashboard', redirectTo: 'sub-work' },
  { path: 'tender-push', component: TenderPushComponent },
  { path: 'document-upload', component: DocumentManagementComponent },
  { path: 'document-management', redirectTo: 'document-upload' },
  { path: 'tender-corrigendum-pull', component: TenderCorrigendumPullComponent },
  { path: 'awarding', component: AocManagementComponent },
  { path: 'aoc-management', redirectTo: 'awarding' },
  { path: 'system-diagnostics', component: SystemDiagnosticsComponent },
  // Backward compatibility redirects for merged menus
  { path: 'metadata-status', redirectTo: 'system-diagnostics' },
  { path: 'json-preview', redirectTo: 'system-diagnostics' },
  { path: 'transaction-history', redirectTo: 'system-diagnostics' },
  { path: 'integration-settings', redirectTo: 'system-diagnostics' }
];
