import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpService } from '../../../core/services/http.service';
import { GEPNIC_CONSTANTS } from '../constants/gepnic.constants';
import { ApiResponse, DiagnosticsFlow } from '../models/gepnic.models';

@Injectable({
  providedIn: 'root'
})
export class EncryptionDebugService {

  private baseUrl = `${GEPNIC_CONSTANTS.BACKEND_API_BASE}/diagnostics`;

  constructor(private http: HttpService) {}

  runSampleFlow(formPayload?: any): Observable<ApiResponse<DiagnosticsFlow>> {
    return this.http.post<ApiResponse<DiagnosticsFlow>>(`${this.baseUrl}/sample-flow`, formPayload || {});
  }
}
