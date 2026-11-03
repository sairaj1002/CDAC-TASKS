import { Component, Inject, ChangeDetectionStrategy } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

// Copy of the QMS DialogComponent (common/dialog), cut down to what a confirmation needs.
// Open it with { data: { title, warning: 'message text', flag: true } }; it closes with data.flag (true) on Ok
// and false on Cancel or on the close icon.
@Component({
    selector: 'app-dialog',
    templateUrl: './dialog.component.html',
    changeDetection: ChangeDetectionStrategy.Eager,
    standalone: false
})
export class DialogComponent {
  confirmationMessageDialogFlag = false;
  showCancelButton = true;
  isAlertMessage = false;

  constructor(public dialogRef: MatDialogRef<DialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData) { }

  ngOnInit(): void {
    if (typeof this.data.warning === 'string') {
      this.confirmationMessageDialogFlag = true;
    }
    if (this.data.cancelButtonFlag !== null && this.data.cancelButtonFlag !== undefined) {
      this.showCancelButton = this.data.cancelButtonFlag;
    }
    if (this.data.isAlertMessage) {
      this.isAlertMessage = true;
    }
  }

  onNoClick(): void {
    this.data.flag = false;
    this.dialogRef.close(this.data.flag);
  }
}

export interface DialogData {
  message?: any;
  warning: any;
  flag: any;
  buttonColor?: any;
  title: any;
  cancelButtonFlag?: boolean;
  isAlertMessage?: boolean;
  remarkFlag?: boolean;
  buttonName?: string;
}
