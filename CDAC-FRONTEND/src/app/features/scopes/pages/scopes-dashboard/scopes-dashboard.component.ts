import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { FormArray, FormBuilder, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';

import {
  BRIDGE_ACTIVITIES,
  BridgeActivity,
  WorkDetailsRequest,
  WorkDetailsResponse,
  WorkScopeResponse,
} from '../../models/scopes.model';
import { ScopesService } from '../../services/scopes.service';

@Component({
  selector: 'app-scopes-dashboard',
  standalone: false,
  templateUrl: './scopes-dashboard.component.html',
  styleUrl: './scopes-dashboard.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ScopesDashboardComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly scopesService = inject(ScopesService);
  private readonly snackBar = inject(MatSnackBar);

  readonly bridgeActivities = BRIDGE_ACTIVITIES;
  readonly workDetails = signal<WorkDetailsResponse[]>([]);
  readonly loading = signal(false);
  readonly saving = signal(false);
  readonly editingWork = signal<WorkDetailsResponse | null>(null);
  readonly submitted = signal(false);

  readonly form = this.formBuilder.group({
    subWorkDetails: this.formBuilder.nonNullable.control('', [
      Validators.required,
      Validators.maxLength(500),
    ]),
    workScopes: this.formBuilder.array([this.createScopeForm()]),
  });

  get workScopes(): FormArray {
    return this.form.controls.workScopes;
  }

  ngOnInit(): void {
    this.loadWorkDetails();
  }

  addScope(): void {
    this.workScopes.push(this.createScopeForm());
  }

  removeScope(index: number): void {
    if (this.workScopes.length > 1) {
      this.workScopes.removeAt(index);
    }
  }

  save(): void {
    this.submitted.set(true);
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const request: WorkDetailsRequest = this.form.getRawValue();
    const editingWork = this.editingWork();
    this.saving.set(true);

    const saveRequest = editingWork
      ? this.scopesService.update(editingWork.workId, request)
      : this.scopesService.create(request);

    saveRequest.subscribe({
      next: (savedWork) => {
        this.showMessage(
          editingWork
            ? `Work ${savedWork.workId} updated successfully.`
            : `Work ${savedWork.workId} created successfully.`,
        );
        this.resetForm();
        this.saving.set(false);
        this.loadWorkDetails();
      },
      error: (error: unknown) => {
        this.showMessage(
          this.getErrorMessage(error, editingWork ? 'Unable to update work.' : 'Unable to create work.'),
          true,
        );
        this.saving.set(false);
      },
    });
  }

  edit(work: WorkDetailsResponse): void {
    this.editingWork.set(work);
    this.form.controls.subWorkDetails.setValue(work.subWorkDetails);
    this.workScopes.clear();
    work.workScopes.forEach((scope) => this.workScopes.push(this.createScopeForm(scope)));
    this.submitted.set(false);
  }

  cancelEdit(): void {
    this.resetForm();
  }

  activityLabel(activity: BridgeActivity): string {
    return this.bridgeActivities.find((option) => option.value === activity)?.label ?? activity;
  }

  delete(work: WorkDetailsResponse): void {
    if (!globalThis.confirm(`Delete work ${work.workId} and all its scopes?`)) {
      return;
    }

    this.scopesService.delete(work.workId).subscribe({
      next: () => {
        this.showMessage(`Work ${work.workId} deleted successfully.`);
        this.loadWorkDetails();
      },
      error: (error: unknown) => {
        this.showMessage(this.getErrorMessage(error, 'Unable to delete work.'), true);
      },
    });
  }

  private loadWorkDetails(): void {
    this.loading.set(true);
    this.scopesService.getAll().subscribe({
      next: (workDetails) => {
        this.workDetails.set(workDetails);
        this.loading.set(false);
      },
      error: (error: unknown) => {
        this.showMessage(this.getErrorMessage(error, 'Unable to load work details.'), true);
        this.loading.set(false);
      },
    });
  }

  private createScopeForm(scope?: WorkScopeResponse) {
    return this.formBuilder.nonNullable.group({
      nameOfRiverNallahBarrier: [
        scope?.nameOfRiverNallahBarrier ?? '',
        [Validators.required, Validators.maxLength(200)],
      ],
      width: [scope?.width ?? 0, [Validators.required, Validators.min(0.001)]],
      length: [scope?.length ?? 0, [Validators.required, Validators.min(0.001)]],
      approachRoad: [
        scope?.approachRoad ?? '',
        [Validators.required, Validators.maxLength(500)],
      ],
      numberOfSpans: [scope?.numberOfSpans ?? 0, [Validators.required, Validators.min(1)]],
      spanLength: [scope?.spanLength ?? 0, [Validators.required, Validators.min(0.001)]],
      bridgeActivity: this.formBuilder.nonNullable.control<BridgeActivity>(
        scope?.bridgeActivity ?? BRIDGE_ACTIVITIES[0].value,
        Validators.required,
      ),
    });
  }

  private resetForm(): void {
    this.editingWork.set(null);
    this.submitted.set(false);
    this.form.reset({ subWorkDetails: '' });
    this.workScopes.clear();
    this.workScopes.push(this.createScopeForm());
  }

  private getErrorMessage(error: unknown, fallback: string): string {
    if (error instanceof HttpErrorResponse && typeof error.error?.message === 'string') {
      return error.error.message;
    }
    return fallback;
  }

  private showMessage(message: string, isError = false): void {
    this.snackBar.open(message, 'Close', {
      duration: 4000,
      panelClass: isError ? ['error-snackbar'] : [],
    });
  }
}
