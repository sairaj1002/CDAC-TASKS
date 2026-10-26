import { Component, inject, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { getApiErrorMessage } from '../../api-response';
import {
  CreateDropdownOptionRequest,
  DropdownOption,
  UpdateDropdownOptionRequest,
} from './dropdown-option';
import { DropdownService } from '../services/dropdown.service';
import { COMMON_CONSTANTS, DROPDOWN_ADMIN_CONSTANTS } from '../../constant/constant';

@Component({
  selector: 'app-dropdown-admin',
  standalone: false,
  templateUrl: './dropdown-admin.component.html',
})
export class DropdownAdminComponent implements OnInit {
  private readonly dropdownService = inject(DropdownService);
  private readonly formBuilder = inject(FormBuilder).nonNullable;
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);

  // Data
  categories: string[] = [];
  selectedCategory: string = DROPDOWN_ADMIN_CONSTANTS.DEFAULT_CATEGORY;
  allOptions: DropdownOption[] = [];
  filteredOptions: DropdownOption[] = [];
  searchQuery = '';
  loading = false;
  saving = false;

  // Edit / Delete State
  optionToEdit: DropdownOption | null = null;
  optionToDelete: DropdownOption | null = null;

  // Form for Add/Edit
  readonly optionForm = this.formBuilder.group({
    category: ['', Validators.required],
    code: ['', [Validators.required, Validators.pattern(DROPDOWN_ADMIN_CONSTANTS.CODE_PATTERN)]],
    label: ['', Validators.required],
    displayOrder: [DROPDOWN_ADMIN_CONSTANTS.DEFAULT_DISPLAY_ORDER as number, [Validators.required, Validators.min(1)]],
    active: [true],
  });

  @ViewChild('deleteConfirmDialog') deleteConfirmDialogTemplate!: TemplateRef<unknown>;

  ngOnInit(): void {
    this.loadCategories();
    this.loadOptions();
  }

  loadCategories(): void {
    this.dropdownService.getCategories().subscribe({
      next: (cats) => {
        this.categories = cats;
      },
      error: (err) => this.showError(err, DROPDOWN_ADMIN_CONSTANTS.MESSAGES.CATEGORIES_LOAD_ERROR),
    });
  }

  loadOptions(): void {
    this.loading = true;
    this.dropdownService.getAll(this.selectedCategory).subscribe({
      next: (options) => {
        this.allOptions = options;
        this.applyFilter();
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        this.showError(err, DROPDOWN_ADMIN_CONSTANTS.MESSAGES.OPTIONS_LOAD_ERROR);
      },
    });
  }

  onCategoryChange(category: string): void {
    this.selectedCategory = category;
    this.loadOptions();
  }

  applyFilter(): void {
    const q = this.searchQuery.trim().toLowerCase();
    if (!q) {
      this.filteredOptions = [...this.allOptions];
    } else {
      this.filteredOptions = this.allOptions.filter(
        (opt) =>
          opt.label.toLowerCase().includes(q) ||
          opt.code.toLowerCase().includes(q) ||
          opt.category.toLowerCase().includes(q),
      );
    }
  }

  submitForm(): void {
    if (this.optionForm.invalid) {
      this.optionForm.markAllAsTouched();
      this.showWarning(DROPDOWN_ADMIN_CONSTANTS.MESSAGES.REQUIRED_FIELDS_WARNING);
      return;
    }

    const val = this.optionForm.getRawValue();
    this.saving = true;

    if (this.optionToEdit) {
      const updatePayload: UpdateDropdownOptionRequest = {
        label: val.label,
        displayOrder: val.displayOrder,
        active: val.active ?? true,
      };

      this.dropdownService
        .update(this.optionToEdit.id, updatePayload, this.optionToEdit.category)
        .subscribe({
          next: () => {
            this.saving = false;
            this.showSuccess(DROPDOWN_ADMIN_CONSTANTS.MESSAGES.updatedSuccess(val.label));
            this.cancelEdit();
            this.loadOptions();
          },
          error: (err) => {
            this.saving = false;
            this.showError(err, DROPDOWN_ADMIN_CONSTANTS.MESSAGES.UPDATE_ERROR);
          },
        });
    } else {
      const createPayload: CreateDropdownOptionRequest = {
        category: val.category.trim().toUpperCase(),
        code: val.code.trim().toUpperCase(),
        label: val.label.trim(),
        displayOrder: val.displayOrder,
        active: val.active ?? true,
      };

      this.dropdownService.create(createPayload).subscribe({
        next: () => {
          this.saving = false;
          this.showSuccess(DROPDOWN_ADMIN_CONSTANTS.MESSAGES.createdSuccess(createPayload.label));
          this.cancelEdit();
          this.loadCategories();
          this.loadOptions();
        },
        error: (err) => {
          this.saving = false;
          this.showError(err, DROPDOWN_ADMIN_CONSTANTS.MESSAGES.CREATE_ERROR);
        },
      });
    }
  }

  editOption(option: DropdownOption): void {
    this.optionToEdit = option;
    this.optionForm.patchValue({
      category: option.category,
      code: option.code,
      label: option.label,
      displayOrder: option.displayOrder,
      active: option.active,
    });
    this.optionForm.controls.category.disable();
    this.optionForm.controls.code.disable();
  }

  cancelEdit(): void {
    this.optionToEdit = null;
    this.optionForm.reset({
      category: this.selectedCategory !== DROPDOWN_ADMIN_CONSTANTS.DEFAULT_CATEGORY ? this.selectedCategory : '',
      code: '',
      label: '',
      displayOrder: DROPDOWN_ADMIN_CONSTANTS.DEFAULT_DISPLAY_ORDER,
      active: true,
    });
    this.optionForm.controls.category.enable();
    this.optionForm.controls.code.enable();
  }

  toggleActive(option: DropdownOption): void {
    const newStatus = !option.active;
    this.dropdownService.toggleActive(option.id, newStatus, option.category).subscribe({
      next: () => {
        option.active = newStatus;
        const msg = newStatus
          ? DROPDOWN_ADMIN_CONSTANTS.MESSAGES.activatedSuccess(option.label)
          : DROPDOWN_ADMIN_CONSTANTS.MESSAGES.deactivatedSuccess(option.label);
        this.showSuccess(msg);
      },
      error: (err) => this.showError(err, DROPDOWN_ADMIN_CONSTANTS.MESSAGES.STATUS_ERROR),
    });
  }

  promptDelete(option: DropdownOption): void {
    this.optionToDelete = option;
    const dialogRef = this.dialog.open(this.deleteConfirmDialogTemplate, {
      width: DROPDOWN_ADMIN_CONSTANTS.CONFIRM_DIALOG_WIDTH,
      panelClass: DROPDOWN_ADMIN_CONSTANTS.CONFIRM_DIALOG_PANEL,
      autoFocus: false,
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (confirmed && this.optionToDelete) {
        this.deleteOption(this.optionToDelete);
      }
      this.optionToDelete = null;
    });
  }

  deleteOption(option: DropdownOption): void {
    this.dropdownService.delete(option.id, option.category).subscribe({
      next: () => {
        this.showSuccess(DROPDOWN_ADMIN_CONSTANTS.MESSAGES.deletedSuccess(option.label));
        this.loadOptions();
      },
      error: (err) => {
        this.showError(err, DROPDOWN_ADMIN_CONSTANTS.MESSAGES.DELETE_ERROR);
      },
    });
  }

  private showSuccess(message: string): void {
    this.snackBar.open(message, COMMON_CONSTANTS.SNACKBAR_ACTION, {
      duration: COMMON_CONSTANTS.SNACKBAR_DURATION_SUCCESS,
      horizontalPosition: COMMON_CONSTANTS.SNACKBAR_HORIZONTAL_CENTER,
      verticalPosition: COMMON_CONSTANTS.SNACKBAR_VERTICAL_TOP,
      panelClass: COMMON_CONSTANTS.SNACKBAR_PANEL_SUCCESS,
    });
  }

  private showWarning(message: string): void {
    this.snackBar.open(message, COMMON_CONSTANTS.SNACKBAR_ACTION, {
      duration: COMMON_CONSTANTS.SNACKBAR_DURATION_WARNING,
      horizontalPosition: COMMON_CONSTANTS.SNACKBAR_HORIZONTAL_CENTER,
      verticalPosition: COMMON_CONSTANTS.SNACKBAR_VERTICAL_TOP,
      panelClass: COMMON_CONSTANTS.SNACKBAR_PANEL_WARNING,
    });
  }

  private showError(error: unknown, fallbackMessage: string): void {
    const message = getApiErrorMessage(error, fallbackMessage);
    this.snackBar.open(message, COMMON_CONSTANTS.SNACKBAR_ACTION, {
      duration: COMMON_CONSTANTS.SNACKBAR_DURATION_ERROR,
      horizontalPosition: COMMON_CONSTANTS.SNACKBAR_HORIZONTAL_CENTER,
      verticalPosition: COMMON_CONSTANTS.SNACKBAR_VERTICAL_TOP,
      panelClass: COMMON_CONSTANTS.SNACKBAR_PANEL_ERROR,
    });
  }
}
