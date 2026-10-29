import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpService } from '../../../core/services/http.service';
import { GEPNIC_CONSTANTS } from '../constants/gepnic.constants';
import { ApiResponse, IntegrationTransaction } from '../models/gepnic.models';

@Injectable({
  providedIn: 'root'
})
export class TransactionService {

  private baseUrl = `${GEPNIC_CONSTANTS.BACKEND_API_BASE}/transactions`;

  constructor(private http: HttpService) {}

  searchTransactions(filters?: any): Observable<ApiResponse<any>> {
    return this.http.get<ApiResponse<any>>(this.baseUrl, filters);
  }

  getTransactionById(id: number): Observable<ApiResponse<IntegrationTransaction>> {
    return this.http.get<ApiResponse<IntegrationTransaction>>(`${this.baseUrl}/${id}`);
  }

  getAuditTrail(id: number): Observable<ApiResponse<any>> {
    return this.http.get<ApiResponse<any>>(`${this.baseUrl}/${id}/audit`);
  }

  retryTransaction(id: number): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(`${this.baseUrl}/${id}/retry`, {});
  }
}
