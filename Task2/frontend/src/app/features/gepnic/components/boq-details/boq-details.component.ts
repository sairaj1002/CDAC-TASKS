import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { TenderDataService } from '../../services/tender-data.service';
import { SnackbarService } from '../../../../shared/services/snackbar.service';

export interface NonSchBoqItem {
  srNo: number;
  schName: string;
  goodsService: string;
  itemCode: string;
  itemDesc: string;
  hsnSac: string;
  uom: string;
  coefficient: string;
  quantity: number;
  type: string;
  rate?: number;
  amount?: number;
}

@Component({
  selector: 'app-boq-details',
  standalone: false,
  templateUrl: './boq-details.component.html',
  styleUrls: ['./boq-details.component.css']
})
export class BoqDetailsComponent implements OnInit {
  @Input() isModal: boolean = false;
  @Input() tenderReference?: string | null;
  @Input() parentWork?: string | null;

  @Output() close = new EventEmitter<void>();

  boqActiveSubTab: 'non-sch' | 'sch' = 'non-sch';
  commonHsnSac: string = '';
  quotePercentType: string = 'At par';
  quotePercentVal: string = '';
  roundOffAmount: number = 0;

  boqTableItems: NonSchBoqItem[] = [
    {
      srNo: 1,
      schName: 'Road Work Schedule A',
      goodsService: 'Works',
      itemCode: 'R-01',
      itemDesc: 'Earthwork in excavation for road embankment',
      hsnSac: '995421',
      uom: 'Cum',
      coefficient: '1.0',
      quantity: 1250,
      type: 'At par',
      rate: 215,
      amount: 268750
    },
    {
      srNo: 2,
      schName: 'Pavement Schedule B',
      goodsService: 'Works',
      itemCode: 'R-02',
      itemDesc: 'Providing and laying Granular Sub-base (GSB)',
      hsnSac: '995421',
      uom: 'Cum',
      coefficient: '1.0',
      quantity: 850,
      type: 'At par',
      rate: 1420,
      amount: 1207000
    },
    {
      srNo: 3,
      schName: 'Surfacing Schedule C',
      goodsService: 'Works',
      itemCode: 'R-03',
      itemDesc: 'Dense Bituminous Macadam (DBM) 50mm thickness',
      hsnSac: '995421',
      uom: 'Sqm',
      coefficient: '1.0',
      quantity: 4200,
      type: 'At par',
      rate: 850,
      amount: 3570000
    }
  ];

  constructor(
    private tenderDataService: TenderDataService,
    private snackbar: SnackbarService
  ) {}

  ngOnInit(): void {
    // If tender data service has total amount, reconcile
  }

  onClose(): void {
    this.close.emit();
  }

  addCommonHsnToAll(): void {
    if (!this.commonHsnSac.trim()) {
      this.snackbar.info('Please enter common HSN/SAC');
      return;
    }
    this.boqTableItems.forEach(item => item.hsnSac = this.commonHsnSac.trim());
    this.snackbar.success(`Applied HSN/SAC "${this.commonHsnSac}" to all items`);
  }

  addQuoteToAll(): void {
    this.boqTableItems.forEach(item => item.type = this.quotePercentType);
    this.snackbar.success(`Applied Quote% "${this.quotePercentType}" to all items`);
  }

  addBoqRow(): void {
    this.boqTableItems.push({
      srNo: this.boqTableItems.length + 1,
      schName: '',
      goodsService: '',
      itemCode: '',
      itemDesc: '',
      hsnSac: this.commonHsnSac || '',
      uom: '',
      coefficient: '1.0',
      quantity: 0,
      type: this.quotePercentType || 'At par',
      rate: 0,
      amount: 0
    });
    this.snackbar.info(`Added row ${this.boqTableItems.length}`);
  }

  removeBoqRow(index: number): void {
    if (this.boqTableItems.length > 1) {
      const removed = this.boqTableItems.splice(index, 1);
      this.boqTableItems.forEach((item, idx) => item.srNo = idx + 1);
      this.snackbar.info(`Removed row ${removed[0]?.srNo}`);
    } else {
      this.snackbar.info('At least one item row is required in the schedule');
    }
  }

  downloadBoqFormat(): void {
    this.snackbar.success('BOQ Schedule Excel format template downloaded');
  }

  triggerBoqUpload(): void {
    this.snackbar.info('Select BOQ Excel template to import');
  }

  saveBoqItemDetails(): void {
    const total = this.totalNonScheduledAmount;
    this.tenderDataService.updateTenderData({
      totalBoqAmount: total,
      estimatedCost: total
    });
    this.snackbar.success(`BOQ Item Details saved. Agreement Amount: ₹ ${this.agreementAmountWithGst.toLocaleString()}`);
    if (this.isModal) {
      setTimeout(() => this.close.emit(), 600);
    }
  }

  get totalNonScheduledAmount(): number {
    return this.boqTableItems.reduce((acc, curr) => {
      const qty = Number(curr.quantity) || 0;
      const rate = Number(curr.rate) || 0;
      return acc + (qty * rate);
    }, 0);
  }

  get totalNonScheduledAmountWithGst(): number {
    return Math.round(this.totalNonScheduledAmount * 1.18 * 100) / 100;
  }

  get agreementAmount(): number {
    return this.totalNonScheduledAmount + (Number(this.roundOffAmount) || 0);
  }

  get agreementAmountWithGst(): number {
    return this.totalNonScheduledAmountWithGst + (Number(this.roundOffAmount) || 0);
  }
}
