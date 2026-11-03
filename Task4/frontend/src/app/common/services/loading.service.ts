import { Injectable, computed, signal } from '@angular/core';

/** Tracks pending work (HTTP calls, start-up) behind the full-screen WAMIS loader. */
@Injectable({
  providedIn: 'root',
})
export class LoadingService {
  private pending = signal(0);

  readonly loading = computed(() => this.pending() > 0);

  show(): void {
    this.pending.update((n) => n + 1);
  }

  hide(): void {
    this.pending.update((n) => Math.max(0, n - 1));
  }
}
