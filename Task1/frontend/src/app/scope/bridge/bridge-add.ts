import { Component, EventEmitter, inject, Input, OnChanges, Output } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { BridgeComponent, BridgeComponentFormValue } from './bridge-component';

@Component({
  selector: 'app-bridge-add',
  standalone: false,
  templateUrl: './bridge-add.html',
})
export class BridgeAdd implements OnChanges {
  private readonly formBuilder = inject(FormBuilder).nonNullable;

  @Input() componentToEdit: BridgeComponent | null = null;
  @Input() serialNumber = 1;
  @Input() components: BridgeComponent[] = [];
  @Input() bridgeActivities: string[] = [];
  @Input() saving = false;
  @Output() saveComponent = new EventEmitter<BridgeComponentFormValue>();
  @Output() cancelEdit = new EventEmitter<void>();
  @Output() editComponent = new EventEmitter<BridgeComponent>();
  @Output() deleteComponent = new EventEmitter<number>();

  readonly componentForm = this.formBuilder.group({
    riverNallahBarrier: ['', [Validators.required, Validators.pattern(/\S/), Validators.maxLength(150)]],
    width: new FormControl<number | null>(null, Validators.min(0)),
    length: new FormControl<number | null>(null, Validators.min(0)),
    approvalRoad: ['', [Validators.required, Validators.pattern(/\S/), Validators.maxLength(150)]],
    numberOfSpans: new FormControl<number | null>(null, [
      Validators.min(0),
      Validators.pattern(/^\d+$/),
    ]),
    spanLength: new FormControl<number | null>(null, Validators.min(0)),
    bridgeActivity: ['', Validators.required],
    otherActivityDescription: new FormControl('', {
      nonNullable: true,
      validators: Validators.maxLength(500),
    }),
  });

  get showOtherActivityDescriptionColumn(): boolean {
    return (
      this.componentForm.controls.bridgeActivity.value === 'Other Activity' ||
      this.components.some((component) => component.bridgeActivity === 'Other Activity')
    );
  }

  ngOnChanges(): void {
    this.syncBridgeActivityControl();

    if (this.componentToEdit) {
      this.componentForm.patchValue({
        riverNallahBarrier: this.componentToEdit.riverNallahBarrier,
        width: this.componentToEdit.width,
        length: this.componentToEdit.length,
        approvalRoad: this.componentToEdit.approvalRoad,
        numberOfSpans: this.componentToEdit.numberOfSpans,
        spanLength: this.componentToEdit.spanLength,
        bridgeActivity: this.componentToEdit.bridgeActivity,
        otherActivityDescription: this.componentToEdit.otherActivityDescription,
      });
      this.onBridgeActivityChange(this.componentToEdit.bridgeActivity);
      return;
    }

    this.componentForm.reset();
    this.onBridgeActivityChange('');
  }

  onBridgeActivityChange(activity: string): void {
    const description = this.componentForm.controls.otherActivityDescription;
    if (activity === 'Other Activity') {
      description.setValidators(Validators.required);
    } else {
      description.clearValidators();
      description.reset('');
    }
    description.updateValueAndValidity();
  }

  private syncBridgeActivityControl(): void {
    const bridgeActivity = this.componentForm.controls.bridgeActivity;
    if (this.bridgeActivities.length === 0 && bridgeActivity.enabled) {
      bridgeActivity.disable({ emitEvent: false });
    } else if (this.bridgeActivities.length > 0 && bridgeActivity.disabled) {
      bridgeActivity.enable({ emitEvent: false });
    }
  }

  submitForm(): void {
    if (this.bridgeActivities.length === 0 || this.componentForm.invalid) {
      this.componentForm.markAllAsTouched();
      return;
    }

    this.saveComponent.emit(this.componentForm.getRawValue());
  }

  cancelEditing(): void {
    this.cancelEdit.emit();
  }
}
