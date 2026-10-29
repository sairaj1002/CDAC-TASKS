import { Component, EventEmitter, Input, Output } from '@angular/core';
import { IntegrationTransaction } from '../../models/gepnic.models';
import { DialogService } from '../../../../shared/services/dialog.service';

@Component({
  selector: 'app-transaction-table',
  standalone: false,
  templateUrl: './transaction-table.component.html'
})
export class TransactionTableComponent {

  @Input() transactions: IntegrationTransaction[] = [];
  @Input() loading: boolean = false;
  @Output() retry = new EventEmitter<IntegrationTransaction>();
  @Output() viewDetails = new EventEmitter<IntegrationTransaction>();

  constructor(private dialogService: DialogService) {}

  openAudit(txn: IntegrationTransaction): void {
    this.dialogService.showAudit(txn.transactionId || String(txn.id));
  }

  openDetails(txn: IntegrationTransaction): void {
    this.dialogService.showDetails('Transaction: ' + txn.transactionId, txn);
  }

  onDelete(txn: IntegrationTransaction): void {
    this.dialogService.confirm({
      title: 'Delete Work?',
      message: 'Are you sure?',
      okText: 'Ok',
      cancelText: 'Cancel'
    }).subscribe(confirmed => {
      if (confirmed) {
        this.transactions = this.transactions.filter(t => t.id !== txn.id);
      }
    });
  }

  onRetry(txn: IntegrationTransaction): void {
    this.retry.emit(txn);
  }
}
