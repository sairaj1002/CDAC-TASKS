import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, FormGroupDirective, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginatorIntl, PageEvent } from '@angular/material/paginator';
import { DialogComponent } from '../../common/dialog/dialog.component';
import { AccessService, FUNCTION, OPERATION } from '../../common/services/access.service';
import { SnackbarService } from '../../common/services/snackbar.service';
import { ItemAuditComponent } from './item-audit/item-audit.component';
import { ItemPaginatorIntl } from './item-paginator-intl';
import { DropdownOption, Item, ItemService } from './item.service';

@Component({
  selector: 'app-item',
  standalone: false,
  // Removes the "Items per page:" text of this page's paginator; the refresh icon takes its place
  providers: [{ provide: MatPaginatorIntl, useClass: ItemPaginatorIntl }],
  // Same as the QMS components: plain fields updated in HTTP callbacks refresh the screen
  changeDetection: ChangeDetectionStrategy.Eager,
  templateUrl: './item.component.html',
})
export class ItemComponent implements OnInit {

  private static readonly ADD_TAB = 0;
  private static readonly VIEW_TAB = 1;

  @ViewChild(FormGroupDirective) private formDirective?: FormGroupDirective;

  itemForm!: FormGroup;
  searchForm!: FormGroup;
  statusList = ['REGISTERED', 'APPROVED', 'CANCELLED'];
  purposeList = [
    { value: 'G', label: 'Grade' },
    { value: 'P', label: 'Photo Upload' },
  ];
  workTypeList: DropdownOption[] = [];
  itemList: Item[] = [];
  displayedColumns = ['srNo', 'itemCode', 'itemDescription', 'workTypeName', 'itemPurpose', 'status', 'actions', 'audit'];

  // Paginator: pageIndex starts at 0; totalCount is the number of items that match the search
  pageIndex = 0;
  pageSize = 10;
  totalCount = 0;

  selectedTab = ItemComponent.ADD_TAB;
  // Set while an existing item is being edited on the Add tab
  editingItemId?: number;
  isSaving = false;

  constructor(private readonly formBuilder: FormBuilder,
    private readonly itemService: ItemService,
    private readonly snackbar: SnackbarService,
    private readonly dialog: MatDialog,
    private readonly access: AccessService) { }

  // What the user may do on Item (see the backend rbac/roles.json); what is not allowed is greyed out
  can(operation: string): boolean {
    return this.access.can(FUNCTION.ITEM, operation);
  }

  // The tab group only holds the tabs the user may use; selectedTab stays 0 = Add, 1 = View
  get groupIndex(): number {
    return this.can(OPERATION.ADD) ? this.selectedTab : 0;
  }

  onGroupIndexChange(index: number): void {
    this.onTabChange(this.can(OPERATION.ADD) ? index : ItemComponent.VIEW_TAB);
  }

  ngOnInit(): void {
    this.itemForm = this.formBuilder.group({
      itemCode: ['', [Validators.required, Validators.maxLength(50)]],
      itemDescription: ['', [Validators.required, Validators.maxLength(500)]],
      workTypeId: [null, Validators.required],
      itemPurpose: [null, Validators.required]
    });
    this.searchForm = this.formBuilder.group({
      itemCode: [''],
      status: [null],
    });
    // A user who may only view opens the screen on the View tab
    if (!this.can(OPERATION.ADD) && this.can(OPERATION.VIEW)) {
      this.selectedTab = ItemComponent.VIEW_TAB;
      this.loadItems();
    }
    this.itemService.getWorkTypes().subscribe({
      next: list => this.workTypeList = list,
      error: (err: HttpErrorResponse) => this.snackbar.showHttpError(err, 'Unable to load work types.'),
    });
  }

  onTabChange(index: number): void {
    // The code itself switches tabs after Edit / Update; only a click by the user needs the load below
    if (index === this.selectedTab) {
      return;
    }
    if ((index === ItemComponent.ADD_TAB && !this.can(OPERATION.ADD)) || (index === ItemComponent.VIEW_TAB && !this.can(OPERATION.VIEW))) {
      return;
    }
    this.selectedTab = index;
    if (index === ItemComponent.VIEW_TAB) {
      this.loadItems();
    }
  }

  loadItems(): void {
    const { itemCode, status } = this.searchForm.value;
    this.itemService.getItems(itemCode, status, this.pageIndex, this.pageSize).subscribe({
      next: page => {
        // The page can be empty if the last item on it was removed; step back to the last page that has items
        if (!page.data.length && page.count > 0 && this.pageIndex > 0) {
          this.pageIndex = Math.ceil(page.count / this.pageSize) - 1;
          this.loadItems();
          return;
        }
        this.itemList = page.data;
        this.totalCount = page.count;
      },
      error: (err: HttpErrorResponse) => this.snackbar.showHttpError(err, 'Unable to load items.'),
    });
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadItems();
  }

  // A new search always starts from the first page
  search(): void {
    this.pageIndex = 0;
    this.loadItems();
  }

  resetSearch(): void {
    this.searchForm.reset({ itemCode: '', status: null });
    this.pageIndex = 0;
    this.loadItems();
  }

  save(): void {
    if (this.itemForm.invalid) {
      this.itemForm.markAllAsTouched();
      return;
    }
    this.isSaving = true;
    const request = this.editingItemId === undefined
      ? this.itemService.saveItem(this.itemForm.value)
      : this.itemService.updateItem(this.editingItemId, this.itemForm.value);
    request.subscribe({
      next: message => {
        this.isSaving = false;
        const wasEditing = this.editingItemId !== undefined;
        this.resetForm();
        this.snackbar.showMessage(message);
        if (wasEditing) {
          this.selectedTab = ItemComponent.VIEW_TAB;
          this.loadItems();
        }
      },
      error: (err: HttpErrorResponse) => {
        this.isSaving = false;
        this.snackbar.showHttpError(err, 'Unable to save item.');
      },
    });
  }

  resetForm(): void {
    this.editingItemId = undefined;
    // resetForm (not reset) also clears the "submitted" state, so no error messages show on the empty form
    this.formDirective?.resetForm({ itemCode: '', itemDescription: '', workTypeId: null, itemPurpose: null });
  }

  // Only a REGISTERED item can be edited, deleted or approved
  canModify(item: Item): boolean {
    return item.status === 'REGISTERED';
  }

  edit(item: Item): void {
    this.editingItemId = item.itemId;
    this.itemForm.reset({
      itemCode: item.itemCode,
      itemDescription: item.itemDescription,
      workTypeId: item.workTypeId,
      itemPurpose: item.itemPurpose,
    });
    this.selectedTab = ItemComponent.ADD_TAB;
  }

  delete(item: Item): void {
    this.confirmAction('Delete Item', `Are you sure you want to delete item ${item.itemCode}?`, () =>
      this.runAction(this.itemService.deleteItem(item.itemId!)));
  }

  approve(item: Item): void {
    this.confirmAction('Approve Item',
      `Are you sure you want to approve item ${item.itemCode}?\nIt cannot be edited after approval.`, () =>
      this.runAction(this.itemService.approveItem(item.itemId!)));
  }

  // Audit Details pop-up: the history of the item, loaded when the tick is clicked
  showAudit(item: Item): void {
    this.itemService.getAudit(item.itemId!).subscribe({
      next: audits => this.dialog.open(ItemAuditComponent, { width: '60rem', maxWidth: '95vw', data: { audits } }),
      error: (err: HttpErrorResponse) => this.snackbar.showHttpError(err, 'Unable to load audit details.'),
    });
  }

  // Asks the user inside the application (the QMS DialogComponent), then runs onConfirm only after Ok
  private confirmAction(title: string, message: string, onConfirm: () => void): void {
    this.dialog.open(DialogComponent, { width: '28rem', data: { title, warning: message, flag: true } })
      .afterClosed().subscribe(confirmed => {
        if (confirmed === true) {
          onConfirm();
        }
      });
  }

  // The text of the pop-up is the one the backend sends
  private runAction(request: ReturnType<ItemService['approveItem']>): void {
    request.subscribe({
      next: message => {
        this.loadItems();
        this.snackbar.showMessage(message);
      },
      error: (err: HttpErrorResponse) => {
        this.loadItems();
        this.snackbar.showHttpError(err, 'Unable to complete the action.');
      },
    });
  }
}
