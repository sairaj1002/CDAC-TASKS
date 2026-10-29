import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export class GepnicValidators {

  static yyyyMMddDate(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) return null;
      const valid = /^[0-9]{8}$/.test(control.value);
      return valid ? null : { invalidDateFormat: 'Format must be yyyyMMdd (e.g. 20261101)' };
    };
  }

  static dateRangeOrder(startDateKey: string, endDateKey: string): ValidatorFn {
    return (formGroup: AbstractControl): ValidationErrors | null => {
      const start = formGroup.get(startDateKey)?.value;
      const end = formGroup.get(endDateKey)?.value;
      if (start && end && new Date(end) < new Date(start)) {
        return { dateOrderInvalid: 'End date cannot be earlier than publish date' };
      }
      return null;
    };
  }
}
