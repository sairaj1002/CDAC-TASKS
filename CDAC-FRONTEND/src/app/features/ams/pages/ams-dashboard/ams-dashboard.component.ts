import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { PageEvent } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';

import { AmsRequest, AmsResponse } from '../../models/ams.model';
import { AmsService } from '../../services/ams.service';

@Component({
  selector: 'app-ams',
  standalone: false,
  templateUrl: './ams-dashboard.component.html',
  styleUrl: './ams-dashboard.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AmsComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly amsService = inject(AmsService);
  private readonly snackBar = inject(MatSnackBar);

  readonly activeTab = signal(0);
  readonly assets = signal<AmsResponse[]>([]);
  readonly loading = signal(false);
  readonly saving = signal(false);
  readonly editingAsset = signal<AmsResponse | null>(null);
  readonly pageIndex = signal(0);
  readonly pageSize = signal(10);

  readonly pagedAssets = computed(() => {
    const start = this.pageIndex() * this.pageSize();
    return this.assets().slice(start, start + this.pageSize());
  });

  readonly displayedColumns = [
    'assetCode',
    'assetName',
    'assetType',
    'location',
    'status',
    'actions',
  ];

  readonly amsForm = this.formBuilder.nonNullable.group({
    assetCode: ['', [Validators.required, Validators.maxLength(50)]],
    assetName: ['', [Validators.required, Validators.maxLength(100)]],
    assetType: ['', Validators.required],
    description: ['', Validators.maxLength(500)],
    location: ['', [Validators.required, Validators.maxLength(150)]],
    status: ['', Validators.required],
  });

  ngOnInit(): void {
    this.loadAssets();
  }

  loadAssets(): void {
    this.loading.set(true);

    this.amsService.getAll().subscribe({
      next: (assets) => {
        this.assets.set(assets);
        const lastPageIndex = Math.max(0, Math.ceil(assets.length / this.pageSize()) - 1);
        this.pageIndex.set(Math.min(this.pageIndex(), lastPageIndex));
        this.loading.set(false);
      },
      error: (error: unknown) => {
        this.showMessage(this.getErrorMessage(error, 'Unable to load assets.'), true);
        this.loading.set(false);
      },
    });
  }

  onSubmit(): void {
    if (this.amsForm.invalid) {
      this.amsForm.markAllAsTouched();
      return;
    }

    const request: AmsRequest = this.amsForm.getRawValue();
    const asset = this.editingAsset();
    this.saving.set(true);

    const saveRequest = asset
      ? this.amsService.update(asset.amsId, request)
      : this.amsService.create(request);

    saveRequest.subscribe({
      next: (savedAsset) => {
        this.showMessage(
          asset
            ? `Asset ${savedAsset.assetCode} updated successfully.`
            : `Asset ${savedAsset.assetCode} created successfully.`,
        );
        this.resetForm();
        this.saving.set(false);
        this.loadAssets();
        this.activeTab.set(1);
      },
      error: (error: unknown) => {
        this.showMessage(
          this.getErrorMessage(error, asset ? 'Unable to update asset.' : 'Unable to create asset.'),
          true,
        );
        this.saving.set(false);
      },
    });
  }

  editAsset(asset: AmsResponse): void {
    this.editingAsset.set(asset);
    this.amsForm.reset({
      assetCode: asset.assetCode,
      assetName: asset.assetName,
      assetType: asset.assetType,
      description: asset.description,
      location: asset.location,
      status: asset.status,
    });
    this.activeTab.set(0);
  }

  deleteAsset(asset: AmsResponse): void {
    const confirmed = window.confirm(`Delete asset "${asset.assetCode}"? This cannot be undone.`);
    if (!confirmed) {
      return;
    }

    this.loading.set(true);

    this.amsService.delete(asset.amsId).subscribe({
      next: () => {
        this.showMessage(`Asset ${asset.assetCode} deleted successfully.`);
        this.loadAssets();
      },
      error: (error: unknown) => {
        this.showMessage(this.getErrorMessage(error, 'Unable to delete asset.'), true);
        this.loading.set(false);
      },
    });
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex.set(event.pageIndex);
    this.pageSize.set(event.pageSize);
  }

  resetForm(): void {
    this.editingAsset.set(null);
    this.amsForm.reset({
      assetCode: '',
      assetName: '',
      assetType: '',
      description: '',
      location: '',
      status: '',
    });
  }

  cancelEdit(): void {
    this.resetForm();
  }

  private showMessage(message: string, isError = false): void {
    this.snackBar.open(message, 'Dismiss', {
      duration: isError ? 6000 : 3500,
      horizontalPosition: 'center',
      panelClass: isError ? ['toast-error'] : ['toast-success'],
      politeness: isError ? 'assertive' : 'polite',
      verticalPosition: 'top',
    });
  }

  private getErrorMessage(error: unknown, fallback: string): string {
    if (error instanceof HttpErrorResponse && typeof error.error?.message === 'string') {
      return error.error.message;
    }

    if (error instanceof Error && error.message) {
      return error.message;
    }

    return fallback;
  }
}
