import { Routes } from '@angular/router';
import { SubWorkDetailsComponent } from './components/sub-work-details/sub-work-details.component';
import { TenderPageComponent } from './pages/tender-page/tender-page.component';
import { TenderPushComponent } from './pages/tender-push/tender-push.component';
import { TenderCorrigendumPullComponent } from './pages/tender-corrigendum-pull/tender-corrigendum-pull.component';
import { AocManagementComponent } from './pages/aoc-management/aoc-management.component';

export const GEPNIC_ROUTES: Routes = [
  { path: '', redirectTo: 'sub-work', pathMatch: 'full' },
  { path: 'sub-work', component: SubWorkDetailsComponent },
  { path: 'tender', component: TenderPageComponent },
  { path: 'pretender', redirectTo: 'tender' },
  { path: 'pre-tender', redirectTo: 'tender' },
  { path: 'pre-tender-configuration', redirectTo: 'tender' },
  { path: 'dashboard', redirectTo: 'sub-work' },
  { path: 'tender-push', component: TenderPushComponent },
  { path: 'document-upload', redirectTo: 'tender-push' },
  { path: 'document-management', redirectTo: 'tender-push' },
  { path: 'tender-corrigendum-pull', component: TenderCorrigendumPullComponent },
  { path: 'awarding', component: AocManagementComponent },
  { path: 'aoc-management', redirectTo: 'awarding' },
  { path: 'system-diagnostics', redirectTo: 'tender-push' }
];
