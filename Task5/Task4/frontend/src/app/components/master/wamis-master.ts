import { ChangeDetectionStrategy, Component, DestroyRef, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router } from '@angular/router';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { PreTenderResponseForm } from '../../common/models/app.models';

/** Pre-Tender page: Add and View tabs, opened at /home/wms/pre-tender/add or /view (like the QMS outer pages). */
@Component({
  selector: 'app-wamis-master',
  changeDetection: ChangeDetectionStrategy.Eager,
  standalone: false,
  templateUrl: './wamis-master.html',
})
export class WamisMasterComponent implements OnInit {
  private static readonly BASE_PATH = '/home/wms/pre-tender';
  private static readonly TABS = ['add', 'view'];

  selectedTabIndex = 0;
  selectedTenderForEdit: PreTenderResponseForm | null = null;

  private readonly destroyRef = inject(DestroyRef);

  constructor(private readonly router: Router, private readonly route: ActivatedRoute) {}

  ngOnInit(): void {
    // the page stays when only the tab in the address changes (menu click, tab click), so follow the parameter
    this.route.paramMap.pipe(takeUntilDestroyed(this.destroyRef)).subscribe(params => {
      this.selectedTabIndex = params.get('tab') === 'view' ? 1 : 0;
    });
  }

  onTabChange(event: MatTabChangeEvent): void {
    this.selectTab(event.index);
  }

  onEditPreTender(tender: PreTenderResponseForm): void {
    this.selectedTenderForEdit = tender;
    this.selectTab(0);
  }

  onFormSubmitted(): void {
    this.selectedTenderForEdit = null;
    this.selectTab(1);
  }

  onFormCancelled(): void {
    this.selectedTenderForEdit = null;
    this.selectTab(1);
  }

  private selectTab(index: number): void {
    if (index === 1) {
      this.selectedTenderForEdit = null;
    }
    this.selectedTabIndex = index;
    this.router.navigate([`${WamisMasterComponent.BASE_PATH}/${WamisMasterComponent.TABS[index]}`]);
  }
}
