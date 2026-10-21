import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-bridge-readonly-field',
  standalone: false,
  templateUrl: './bridge-readonly-field.html',
})
export class BridgeReadonlyField {
  @Input({ required: true }) label = '';
  @Input() value: string | number | null = null;

  get displayValue(): string | number {
    return this.value ?? '-';
  }
}
