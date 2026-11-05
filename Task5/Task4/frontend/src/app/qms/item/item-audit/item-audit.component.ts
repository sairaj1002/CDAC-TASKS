import { ChangeDetectionStrategy, Component, Inject, ViewChild } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { ItemAudit } from '../item.service';

// Audit Details pop-up: one line per change made to the item. Open it with { data: { audits: ItemAudit[] } }
@Component({
  selector: 'app-item-audit',
  standalone: false,
  changeDetection: ChangeDetectionStrategy.Eager,
  templateUrl: './item-audit.component.html',
})
export class ItemAuditComponent {

  displayedColumns = ['remark', 'name', 'employeeName', 'designation', 'timestamp'];
  dataSource: MatTableDataSource<ItemAudit>;

  // The paginator is inside the pop-up, so it is only available after the view is built
  @ViewChild(MatPaginator) set paginator(paginator: MatPaginator) {
    this.dataSource.paginator = paginator;
  }

  constructor(@Inject(MAT_DIALOG_DATA) public data: { audits: ItemAudit[] }) {
    this.dataSource = new MatTableDataSource(data.audits);
  }
}
