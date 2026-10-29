import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpService } from '../../../core/services/http.service';
import { GEPNIC_CONSTANTS } from '../constants/gepnic.constants';
import { ApiResponse, TenderResponse, CorrigendumResponse } from '../models/gepnic.models';

@Injectable({
  providedIn: 'root'
})
export class TenderIntegrationService {

  private baseUrl = GEPNIC_CONSTANTS.BACKEND_API_BASE;

  constructor(private http: HttpService) {}

  pushTender(payload: any): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(`${this.baseUrl}/tenders/push`, payload);
  }

  pullTenders(payload: any): Observable<ApiResponse<TenderResponse[]>> {
    return this.http.post<ApiResponse<TenderResponse[]>>(`${this.baseUrl}/tenders/pull`, payload);
  }

  pullCorrigendums(payload: any): Observable<ApiResponse<CorrigendumResponse[]>> {
    return this.http.post<ApiResponse<CorrigendumResponse[]>>(`${this.baseUrl}/corrigendums/pull`, payload);
  }
}
