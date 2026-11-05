import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { MAT_SNACK_BAR_DATA, MatSnackBarRef } from '@angular/material/snack-bar';

export interface CustomSnackbarData {
  message: string;
  type: 'error' | 'success' | 'info' | 'warning';
}

@Component({
  selector: 'app-custom-snackbar',
  changeDetection: ChangeDetectionStrategy.Eager,
  standalone: false,
  templateUrl: './custom-snackbar.html',
})
export class CustomSnackbarComponent {
  copied = false;

  constructor(
    @Inject(MAT_SNACK_BAR_DATA) public data: CustomSnackbarData,
    public snackBarRef: MatSnackBarRef<CustomSnackbarComponent>
  ) {}

  copyMessage(): void {
    if (this.data && this.data.message) {
      navigator.clipboard.writeText(this.data.message).then(() => {
        this.copied = true;
        setTimeout(() => (this.copied = false), 2000);
      });
    }
  }
}

