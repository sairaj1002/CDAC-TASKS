import { ChangeDetectionStrategy, Component, DestroyRef, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router } from '@angular/router';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { PostTenderService } from '../../common/services/post-tender.service';
import { NotificationService } from '../../common/services/notification.service';
import { PostTenderDbParameters } from '../../common/models/app.models';

/**
 * Post-Tender Master Component: Outer shell with AOC Details, Corrigendum and Negotiation tabs.
 * Corrigendum and Negotiation tabs are enabled/disabled dynamically based on DB parameters.
 */
@Component({
  selector: 'app-post-tender-master',
  standalone: false,
  changeDetection: ChangeDetectionStrategy.Eager,
  templateUrl: './post-tender-master.html',
})
export class PostTenderMasterComponent implements OnInit {
  selectedTabIndex = 0;

  // DB Parameters loaded from backend database
  isCorrigendumTabEnabled = true;
  isNegotiationTabEnabled = true;
  loadingParams = false;

  // Expandable parameter config bar
  configPanelOpen = false;

  private readonly destroyRef = inject(DestroyRef);

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly postTenderService: PostTenderService,
    private readonly notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.loadDbParameters();

    this.route.paramMap.pipe(takeUntilDestroyed(this.destroyRef)).subscribe((params) => {
      const tab = params.get('tab');
      if (tab === 'corrigendum' && this.isCorrigendumTabEnabled) {
        this.selectedTabIndex = 1;
      } else if (tab === 'negotiation' && this.isNegotiationTabEnabled) {
        this.selectedTabIndex = this.isCorrigendumTabEnabled ? 2 : 1;
      } else {
        this.selectedTabIndex = 0;
      }
    });
  }

  loadDbParameters(): void {
    this.loadingParams = true;
    this.postTenderService.getDbParameters().subscribe({
      next: (params: PostTenderDbParameters) => {
        this.isCorrigendumTabEnabled = params.enableCorrigendumTab;
        this.isNegotiationTabEnabled = params.enableNegotiationTab;
        this.loadingParams = false;
      },
      error: () => {
        this.loadingParams = false;
      },
    });
  }

  onTabChange(event: MatTabChangeEvent): void {
    this.selectedTabIndex = event.index;
  }

  toggleParameter(paramName: 'ENABLE_CORRIGENDUM_TAB' | 'ENABLE_NEGOTIATION_TAB'): void {
    const nextVal = paramName === 'ENABLE_CORRIGENDUM_TAB' ? !this.isCorrigendumTabEnabled : !this.isNegotiationTabEnabled;

    this.postTenderService.toggleDbParameter(paramName, nextVal).subscribe({
      next: (params) => {
        this.isCorrigendumTabEnabled = params.enableCorrigendumTab;
        this.isNegotiationTabEnabled = params.enableNegotiationTab;

        const label = paramName === 'ENABLE_CORRIGENDUM_TAB' ? 'Corrigendum Tab' : 'Negotiation Tab';
        const state = nextVal ? 'Enabled' : 'Disabled';
        this.notificationService.showSuccess(`DB Parameter [${paramName}] updated: ${label} is now ${state}`);

        if (this.selectedTabIndex > 0) {
          this.selectedTabIndex = 0;
        }
      },
    });
  }
}

