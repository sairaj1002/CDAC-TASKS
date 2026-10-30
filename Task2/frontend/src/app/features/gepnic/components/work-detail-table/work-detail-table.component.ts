import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

export interface WorkDetailRecord {
  id: number;
  workRegistrationNumber: string;
  nameOfWork: string;
  workType: string;
  workStatus: string;
  workGisCode?: string;
  budgetYear: string;
  status: string;
}

@Component({
  selector: 'app-work-detail-table',
  standalone: false,
  templateUrl: './work-detail-table.component.html',
  styleUrls: ['./work-detail-table.component.css']
})
export class WorkDetailTableComponent implements OnInit {

  @Input() selectedWorkNumber: string = '2026100014';
  @Output() workSelect = new EventEmitter<WorkDetailRecord>();
  @Output() workEdit = new EventEmitter<WorkDetailRecord>();
  @Output() workDelete = new EventEmitter<WorkDetailRecord>();
  @Output() workClose = new EventEmitter<WorkDetailRecord>();
  @Output() workAttachment = new EventEmitter<WorkDetailRecord>();
  @Output() workSubWorks = new EventEmitter<WorkDetailRecord>();
  @Output() workOverview = new EventEmitter<WorkDetailRecord>();
  @Output() workAuditDetails = new EventEmitter<WorkDetailRecord>();

  works: WorkDetailRecord[] = [
    {
      id: 1,
      workRegistrationNumber: '2026100014',
      nameOfWork: 'Repair, renovation and special repairs to existing Government buildings , Including waterproofing, flooring and painting',
      workType: 'MINOR',
      workStatus: 'COMPLETED',
      workGisCode: '',
      budgetYear: '2026-2027',
      status: 'APPROVED'
    }
  ];

  ngOnInit(): void {}

  onSelect(work: WorkDetailRecord): void {
    this.workSelect.emit(work);
  }

  onEdit(work: WorkDetailRecord): void {
    this.workEdit.emit(work);
  }

  onDelete(work: WorkDetailRecord): void {
    this.workDelete.emit(work);
  }

  onClose(work: WorkDetailRecord): void {
    this.workClose.emit(work);
  }

  onAttachment(work: WorkDetailRecord): void {
    this.workAttachment.emit(work);
  }

  onSubWorks(work: WorkDetailRecord): void {
    this.workSubWorks.emit(work);
  }

  onOverview(work: WorkDetailRecord): void {
    this.workOverview.emit(work);
  }

  onAuditDetails(work: WorkDetailRecord): void {
    this.workAuditDetails.emit(work);
  }
}

