import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export interface ConfirmDialogOptions {
  title?: string;
  message?: string;
  okText?: string;
  cancelText?: string;
}

export interface ConfirmDialogState extends ConfirmDialogOptions {
  isOpen: boolean;
  resolve?: (confirmed: boolean) => void;
}

const DEFAULT_OPTIONS: Required<ConfirmDialogOptions> = {
  title: 'Delete Work?',
  message: 'Are you sure?',
  okText: 'Ok',
  cancelText: 'Cancel',
};

@Injectable({
  providedIn: 'root',
})
export class ConfirmDialogService {
  private readonly stateSubject = new BehaviorSubject<ConfirmDialogState>({
    isOpen: false,
    ...DEFAULT_OPTIONS,
  });

  readonly dialogState$: Observable<ConfirmDialogState> = this.stateSubject.asObservable();

  confirm(options?: ConfirmDialogOptions): Promise<boolean> {
    return new Promise<boolean>(resolve => {
      this.stateSubject.next({
        isOpen: true,
        title: options?.title || DEFAULT_OPTIONS.title,
        message: options?.message || DEFAULT_OPTIONS.message,
        okText: options?.okText || DEFAULT_OPTIONS.okText,
        cancelText: options?.cancelText || DEFAULT_OPTIONS.cancelText,
        resolve: (confirmed: boolean) => {
          this.close();
          resolve(confirmed);
        },
      });
    });
  }

  confirmAction(): void {
    const state = this.stateSubject.getValue();
    if (state.resolve) {
      state.resolve(true);
    }
    this.close();
  }

  cancelAction(): void {
    const state = this.stateSubject.getValue();
    if (state.resolve) {
      state.resolve(false);
    }
    this.close();
  }

  private close(): void {
    this.stateSubject.next({
      isOpen: false,
      ...DEFAULT_OPTIONS,
      resolve: undefined,
    });
  }
}

