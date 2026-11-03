import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { APP_CONSTANTS } from '../../../common/constants/app.constants';
import { DropdownMasterForm, DropdownMasterResponseForm } from '../../../common/models/app.models';
import { DropdownService } from '../../../common/services/dropdown.service';
import { NotificationService } from '../../../common/services/notification.service';
import { ConfirmDialogComponent } from '../../shared/confirm-dialog/confirm-dialog';

@Component({
  selector: 'app-dropdown-master',
  standalone: false,
  templateUrl: './dropdown-master.html',
})
export class DropdownMasterChildComponent implements OnInit {
  categories = Object.values(APP_CONSTANTS.DROPDOWN_CATEGORIES);
  selectedCategory = 'ALL';
  loading = false;
  saving = false;
  isEditMode = false;
  editingId: number | null = null;

  displayedColumns = [
    'srNo',
    'category',
    'code',
    'displayName',
    'displayOrder',
    'active',
    'audit',
    'action',
  ];

  dataSource = new MatTableDataSource<DropdownMasterResponseForm>([]);
  allOptions: DropdownMasterResponseForm[] = [];

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  formData: DropdownMasterForm = {
    category: APP_CONSTANTS.DROPDOWN_CATEGORIES.TENDER_TYPE,
    code: '',
    displayName: '',
    description: '',
    displayOrder: 1,
    active: true,
  };

  constructor(
    private dropdownService: DropdownService,
    private notificationService: NotificationService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadAllOptions();
  }

  loadAllOptions(): void {
    this.loading = true;
    this.dropdownService.getAllGrouped().subscribe({
      next: (grouped) => {
        const flatList: DropdownMasterResponseForm[] = [];
        Object.keys(grouped).forEach((cat) => {
          flatList.push(...grouped[cat]);
        });
        this.allOptions = flatList;
        this.filterByCategory();
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        this.notificationService.showError('Failed to load master dropdown options: ' + (err.error?.message || err.message));
      },
    });
  }

  onCategoryChange(): void {
    this.filterByCategory();
  }

  private filterByCategory(): void {
    let filtered = this.allOptions;
    if (this.selectedCategory !== 'ALL') {
      filtered = this.allOptions.filter((opt) => opt.category === this.selectedCategory);
    }
    this.dataSource.data = filtered;
    this.dataSource.paginator = this.paginator;
  }

  onSubmit(): void {
    if (!this.formData.code || !this.formData.displayName) {
      this.notificationService.showError('Option code and display name are required fields.');
      return;
    }

    const confirmData = this.isEditMode
      ? {
          title: 'Confirm Update Master Option',
          message: `Are you sure you want to update master option "${this.formData.displayName}"?`,
          confirmText: 'Yes, Update',
          cancelText: 'Cancel',
          isDestructive: false,
        }
      : {
          title: 'Confirm Save Master Option',
          message: `Are you sure you want to save new master option "${this.formData.displayName}"?`,
          confirmText: 'Yes, Save',
          cancelText: 'Cancel',
          isDestructive: false,
        };

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: confirmData,
      width: '400px',
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (confirmed) {
        this.saving = true;
        if (this.isEditMode && this.editingId != null) {
          this.dropdownService.updateOption(this.editingId, this.formData).subscribe({
            next: (updated) => {
              this.saving = false;
              this.notificationService.showSuccess(`Master option "${updated.displayName}" updated successfully.`);
              this.resetForm();
              this.loadAllOptions();
            },
            error: (err) => {
              this.saving = false;
              this.notificationService.showError('Update failed: ' + (err.error?.message || err.message));
            },
          });
        } else {
          this.dropdownService.createOption(this.formData).subscribe({
            next: (created) => {
              this.saving = false;
              this.notificationService.showSuccess(`Master option "${created.displayName}" created successfully.`);
              this.resetForm();
              this.loadAllOptions();
            },
            error: (err) => {
              this.saving = false;
              this.notificationService.showError('Creation failed: ' + (err.error?.message || err.message));
            },
          });
        }
      }
    });
  }

  onEdit(row: DropdownMasterResponseForm): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Confirm Edit Action',
        message: `Load master option "${row.displayName}" for editing?`,
        confirmText: 'Edit Option',
        cancelText: 'Cancel',
        isDestructive: false,
      },
      width: '400px',
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (confirmed) {
        this.isEditMode = true;
        this.editingId = row.id;
        this.formData = {
          category: row.category,
          code: row.code,
          displayName: row.displayName,
          description: row.description || '',
          displayOrder: row.displayOrder,
          active: row.active,
        };
      }
    });
  }

  onToggleActive(row: DropdownMasterResponseForm): void {
    const newStatus = !row.active;
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Confirm Status Change',
        message: `Do you want to ${newStatus ? 'activate' : 'deactivate'} master option "${row.displayName}"?`,
        confirmText: newStatus ? 'Activate' : 'Deactivate',
        cancelText: 'Cancel',
        isDestructive: !newStatus,
      },
      width: '400px',
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (confirmed) {
        this.loading = true;
        const updatePayload: DropdownMasterForm = {
          category: row.category,
          code: row.code,
          displayName: row.displayName,
          description: row.description,
          displayOrder: row.displayOrder,
          active: newStatus,
        };
        this.dropdownService.updateOption(row.id, updatePayload).subscribe({
          next: () => {
            this.loading = false;
            this.notificationService.showSuccess(
              `Status for "${row.displayName}" changed to ${newStatus ? 'ACTIVE' : 'INACTIVE'}.`
            );
            this.loadAllOptions();
          },
          error: (err) => {
            this.loading = false;
            this.notificationService.showError('Failed to change status: ' + (err.error?.message || err.message));
          },
        });
      }
    });
  }

  onDelete(row: DropdownMasterResponseForm): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Confirm Delete Master Option',
        message: `Are you sure you want to delete master option "${row.displayName}" (${row.code})? This may affect pre-tenders referencing this option.`,
        confirmText: 'Delete Permanently',
        cancelText: 'Cancel',
        isDestructive: true,
      },
      width: '420px',
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (confirmed) {
        this.loading = true;
        this.dropdownService.deleteOption(row.id).subscribe({
          next: () => {
            this.loading = false;
            this.notificationService.showSuccess(`Master option "${row.displayName}" deleted successfully.`);
            if (this.editingId === row.id) {
              this.resetForm();
            }
            this.loadAllOptions();
          },
          error: (err) => {
            this.loading = false;
            this.notificationService.showError('Delete failed: ' + (err.error?.message || err.message));
          },
        });
      }
    });
  }

  resetForm(): void {
    this.isEditMode = false;
    this.editingId = null;
    this.formData = {
      category: this.selectedCategory !== 'ALL' ? this.selectedCategory : APP_CONSTANTS.DROPDOWN_CATEGORIES.TENDER_TYPE,
      code: '',
      displayName: '',
      description: '',
      displayOrder: this.dataSource.data.length + 1,
      active: true,
    };
  }
}

