import { Component, EventEmitter, Input, Output } from '@angular/core';

export interface DataTableColumn {
  key: string;
  label: string;
  type?: 'text' | 'input' | 'radio' | 'chip' | 'actions' | 'date' | 'number';
  width?: string;
  align?: 'left' | 'center' | 'right';
  required?: boolean;
  tooltip?: string;
}

@Component({
  selector: 'app-data-table',
  standalone: false,
  templateUrl: './data-table.component.html'
})
export class DataTableComponent {

  @Input() columns: DataTableColumn[] = [];
  @Input() data: any[] = [];
  @Input() note: string = '';
  @Input() emptyMessage: string = 'No records found.';
  @Input() actionButtonText: string = 'Modify';
  @Input() showActionButton: boolean = true;
  @Input() loading: boolean = false;

  @Output() actionClick = new EventEmitter<{ action: string; row: any; index: number }>();
  @Output() modifyClick = new EventEmitter<void>();
  @Output() cellChange = new EventEmitter<{ row: any; key: string; value: any; index: number }>();
  @Output() radioChange = new EventEmitter<{ row: any; key: string; value: boolean; index: number }>();

  onAction(action: string, row: any, index: number): void {
    this.actionClick.emit({ action, row, index });
  }

  onModify(): void {
    this.modifyClick.emit();
  }

  onCellInput(row: any, key: string, event: Event, index: number): void {
    const target = event.target as HTMLInputElement;
    row[key] = target.value;
    this.cellChange.emit({ row, key, value: target.value, index });
  }

  onRadio(row: any, key: string, value: boolean, index: number): void {
    row[key] = value;
    this.radioChange.emit({ row, key, value, index });
  }
}
