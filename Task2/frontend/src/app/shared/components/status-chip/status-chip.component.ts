import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-status-chip',
  standalone: false,
  templateUrl: './status-chip.component.html'
})
export class StatusChipComponent {
  @Input() status: string = 'PENDING';

  get statusClass(): string {
    const s = (this.status || '').toUpperCase();
    if (s === 'SUCCESS' || s === 'PUBLISHED' || s === 'ACTIVE' || s === 'SYNCHRONIZED') {
      return 'wamis-status-success';
    } else if (s === 'FAILED' || s === 'REJECTED' || s === 'ERROR') {
      return 'wamis-status-failed';
    } else if (s === 'IN_PROGRESS' || s === 'TRANSMITTED' || s === 'RETRIED') {
      return 'wamis-status-progress';
    } else {
      return 'wamis-status-pending';
    }
  }
}
