import { Component, inject, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { getApiErrorMessage } from '../../api-response';
import { BridgeApiService } from '../bridge/bridge-api.service';
import { BridgeComponent, BridgeComponentFormValue } from '../bridge/bridge-component';

@Component({
  selector: 'app-bridge-master',
  standalone: false,
  templateUrl: './scope-master.html',
})
export class ScopeMaster implements OnInit {
  private readonly bridgeApi = inject(BridgeApiService);
  private readonly snackBar = inject(MatSnackBar);

  bridgeComponents: BridgeComponent[] = [];
  bridgeActivities: string[] = [];
  componentToEdit: BridgeComponent | null = null;
  selectedTabIndex = 0;
  saving = false;

  ngOnInit(): void {
    this.bridgeApi.getComponents().subscribe({
      next: (components) => (this.bridgeComponents = this.sortById(components)),
      error: (error: unknown) => this.showError(error, 'Bridge components could not be loaded.'),
    });
    this.bridgeApi.getBridgeActivities().subscribe({
      next: (activities) => (this.bridgeActivities = activities),
      error: (error: unknown) =>
        this.showError(error, 'Bridge activity options could not be loaded.'),
    });
  }

  get serialNumber(): number {
    const editingIndex = this.componentToEdit
      ? this.bridgeComponents.findIndex(({ id }) => id === this.componentToEdit?.id)
      : -1;

    return editingIndex >= 0 ? editingIndex + 1 : this.bridgeComponents.length + 1;
  }

  saveComponent(formValue: BridgeComponentFormValue): void {
    const editing = this.componentToEdit;
    const request = {
      ...formValue,
      officeId: editing?.officeId ?? null,
      workId: editing?.workId ?? null,
    };
    this.saving = true;

    const saveRequest = editing
      ? this.bridgeApi.updateComponent(editing.id, request)
      : this.bridgeApi.createComponent(request);

    saveRequest.subscribe({
      next: (component) => {
        this.bridgeComponents = this.sortById(
          editing
            ? this.bridgeComponents.map((existing) =>
                existing.id === component.id ? component : existing,
              )
            : [...this.bridgeComponents, component],
        );
        this.componentToEdit = null;
        this.selectedTabIndex = 1;
        this.saving = false;
        this.showSuccess(editing ? 'Bridge component updated.' : 'Bridge component created.');
      },
      error: (error: unknown) => {
        this.saving = false;
        this.showError(error, 'Bridge component could not be saved.');
      },
    });
  }

  editComponent(component: BridgeComponent): void {
    this.componentToEdit = component;
    this.selectedTabIndex = 0;
  }

  cancelEdit(): void {
    this.componentToEdit = null;
  }

  deleteComponent(id: number): void {
    this.bridgeApi.deleteComponent(id).subscribe({
      next: () => {
        this.bridgeComponents = this.bridgeComponents.filter((component) => component.id !== id);
        if (this.componentToEdit?.id === id) {
          this.componentToEdit = null;
        }
        this.showSuccess('Bridge component deleted.');
      },
      error: (error: unknown) => this.showError(error, 'Bridge component could not be deleted.'),
    });
  }

  private showError(error: unknown, fallbackMessage: string): void {
    const message = getApiErrorMessage(error, fallbackMessage);
    this.snackBar.open(message, 'Close', {
      duration: 6000,
      horizontalPosition: 'center',
      verticalPosition: 'top',
      panelClass: ['snackbar-warn', 'snackbar-big'],
    });
  }

  private showSuccess(message: string): void {
    this.snackBar.open(message, 'Close', {
      duration: 3000,
      horizontalPosition: 'end',
      verticalPosition: 'top',
      panelClass: ['snackbar-warn', 'snackbar-big'],
    });
  }

  private sortById(components: BridgeComponent[]): BridgeComponent[] {
    return [...components].sort((left, right) => left.id - right.id);
  }
}
