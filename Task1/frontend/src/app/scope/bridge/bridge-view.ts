import { Component, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { BridgeComponent } from './bridge-component';
import { BridgeAuditDialog } from './bridge-audit-dialog';
import { BridgeDetailsDialog } from './bridge-details-dialog';

@Component({
  selector: 'app-bridge-view',
  standalone: false,
  templateUrl: './bridge-view.html',
})
export class BridgeView {
  @Output() editComponent = new EventEmitter<BridgeComponent>();
  @Output() deleteComponent = new EventEmitter<number>();

  readonly displayedColumns = [
    'serialNumber',
    'riverNallahBarrier',
    'width',
    'length',
    'approvalRoad',
    'numberOfSpans',
    'spanLength',
    'bridgeActivity',
    'otherActivityDescription',
    'audit',
    'actions',
  ];
  readonly dataSource = new MatTableDataSource<BridgeComponent>([]);

  constructor(private readonly dialog: MatDialog) {}

  @Input()
  set components(components: BridgeComponent[]) {
    this.dataSource.data = components;
  }

  @ViewChild(MatPaginator)
  set paginator(paginator: MatPaginator | undefined) {
    if (paginator) {
      this.dataSource.paginator = paginator;
    }
  }

  viewComponent(component: BridgeComponent): void {
    this.dialog.open(BridgeDetailsDialog, {
      width: '75vw',
      height: '75vh',
      maxWidth: '75vw',
      maxHeight: '75vh',
      data: {
        component,
        serialNumber: this.dataSource.data.findIndex(({ id }) => id === component.id) + 1,
      },
    });
  }

  getSerialNumber(index: number): number {
    const paginator = this.dataSource.paginator;
    return (
      (paginator?.pageIndex ?? 0) * (paginator?.pageSize ?? this.dataSource.data.length) + index + 1
    );
  }

  edit(component: BridgeComponent): void {
    this.editComponent.emit(component);
  }

  delete(component: BridgeComponent): void {
    this.deleteComponent.emit(component.id);
  }

  viewAudit(component: BridgeComponent): void {
    this.dialog.open(BridgeAuditDialog, {
      width: '75vw',
      height: '75vh',
      maxWidth: '75vw',
      maxHeight: '75vh',
      data: { id: component.id, riverNallahBarrier: component.riverNallahBarrier },
    });
  }
}
