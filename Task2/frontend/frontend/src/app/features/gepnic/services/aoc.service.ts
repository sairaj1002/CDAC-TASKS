import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpService } from '../../../core/services/http.service';
import { GEPNIC_CONSTANTS } from '../constants/gepnic.constants';
import { ApiResponse, AocResponse } from '../models/gepnic.models';

@Injectable({
  providedIn: 'root'
})
export class AocService {

  private baseUrl = `${GEPNIC_CONSTANTS.BACKEND_API_BASE}/aoc`;

  constructor(private http: HttpService) {}

  pullAoc(form: any): Observable<ApiResponse<AocResponse[]>> {
    return this.http.post<ApiResponse<AocResponse[]>>(`${this.baseUrl}/pull`, form);
  }

  downloadAocDocument(downloadForm: any): Observable<Blob> {
    return this.http.post<Blob>(`${this.baseUrl}/documents/download`, downloadForm);
  }
}
