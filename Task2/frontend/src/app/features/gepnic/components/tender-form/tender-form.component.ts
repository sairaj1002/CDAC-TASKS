import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { GepnicForms } from '../../forms/gepnic-forms';
import { TenderDocumentItem } from '../../models/gepnic.models';

@Component({
  selector: 'app-tender-form',
  standalone: false,
  templateUrl: './tender-form.component.html'
})
export class TenderFormComponent implements OnInit {

  @Input() initialData: any = null;
  @Output() formSubmit = new EventEmitter<any>();

  tenderForm!: FormGroup;
  documents: TenderDocumentItem[] = [];

  ngOnInit(): void {
    this.tenderForm = GepnicForms.createTenderPushForm();
    if (this.initialData) {
      this.tenderForm.patchValue(this.initialData);
    } else {
      // Default demo values
      this.tenderForm.patchValue({
        wamisTenderId: 'WAMIS-TND-2026-000123',
        officeId: 'OFF-001',
        tenderTitle: 'Road Improvement Work in Pune Division',
        estimatedCost: 2500000,
        tenderFee: 1500,
        emdAmount: 50000,
        publishDate: '2026-11-01T10:00',
        submissionEndDate: '2026-11-20T17:00',
        workDescription: 'Asphalt resurfacing and drainage strengthening on MDR-12'
      });
      this.documents = [
        { documentId: 'DOC-101', fileName: 'tender-notice.pdf', contentType: '.pdf', fileSize: 15400, docCode: 'NITDOC' },
        { documentId: 'DOC-102', fileName: 'financial-boq.xls', contentType: '.xls', fileSize: 28600, docCode: 'BOQDOC' }
      ];
    }
  }

  addDocument(): void {
    const nextId = this.documents.length + 1;
    this.documents.push({
      documentId: 'DOC-' + (100 + nextId),
      fileName: 'specification-' + nextId + '.pdf',
      contentType: '.pdf',
      fileSize: 12400,
      docCode: 'SPEC_DOC'
    });
  }

  removeDocument(index: number): void {
    this.documents.splice(index, 1);
  }

  submit(): void {
    if (this.tenderForm.valid) {
      this.formSubmit.emit({
        ...this.tenderForm.value,
        documents: this.documents
      });
    } else {
      this.tenderForm.markAllAsTouched();
    }
  }
}
