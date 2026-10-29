import { FormGroup, FormControl, Validators } from '@angular/forms';

export class GepnicForms {

  static createTenderPushForm(): FormGroup {
    return new FormGroup({
      wamisTenderId: new FormControl('', [Validators.required]),
      officeId: new FormControl('', [Validators.required]),
      tenderTitle: new FormControl('', [Validators.required, Validators.minLength(5)]),
      estimatedCost: new FormControl(null, [Validators.required, Validators.min(1)]),
      tenderFee: new FormControl(0, [Validators.min(0)]),
      emdAmount: new FormControl(0, [Validators.min(0)]),
      publishDate: new FormControl('', [Validators.required]),
      submissionEndDate: new FormControl('', [Validators.required]),
      workDescription: new FormControl('')
    });
  }

  static createTenderPullForm(): FormGroup {
    return new FormGroup({
      publishedDate: new FormControl('', [Validators.required, Validators.pattern(/^[0-9]{8}$/)]),
      tenderReference: new FormControl(''),
      officeCode: new FormControl('')
    });
  }

  static createCorrigendumPullForm(): FormGroup {
    return new FormGroup({
      publishedDate: new FormControl('', [Validators.required, Validators.pattern(/^[0-9]{8}$/)]),
      tenderReference: new FormControl('')
    });
  }

  static createAocPullForm(): FormGroup {
    return new FormGroup({
      createdDate: new FormControl('', [Validators.required, Validators.pattern(/^[0-9]{8}$/)]),
      tenderReference: new FormControl('')
    });
  }

  static createDocumentUploadForm(): FormGroup {
    return new FormGroup({
      uniqueFileNo: new FormControl('', [Validators.required]),
      docUniqueId: new FormControl('1', [Validators.required]),
      tenderReference: new FormControl('', [Validators.required]),
      docCode: new FormControl('NITDOC', [Validators.required]),
      docType: new FormControl('.pdf', [Validators.required]),
      fileName: new FormControl('', [Validators.required])
    });
  }

  static createSettingsForm(): FormGroup {
    return new FormGroup({
      environmentName: new FormControl('dev', [Validators.required]),
      baseUrl: new FormControl('https://demoeproc.nic.in', [Validators.required]),
      uniqueUserId: new FormControl('WAMIS-REGISTERED-USER-ID', [Validators.required]),
      keyId: new FormControl('GEPNIC-CERTIFICATE-THUMBPRINT-001', [Validators.required]),
      active: new FormControl(true),
      connectTimeoutMs: new FormControl(10000, [Validators.required]),
      readTimeoutMs: new FormControl(30000, [Validators.required])
    });
  }
}
