import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { APP_CONSTANTS } from '../constants/app.constants';
import {
  AuditEventResponseForm,
  IntegrationJobResponseForm,
  IntegrationResult,
  PortalResponse,
} from '../models/app.models';

@Injectable({
  providedIn: 'root',
})
export class GePNICService {
  private readonly baseUrl = `${APP_CONSTANTS.API_BASE_URL}${APP_CONSTANTS.ENDPOINTS.GEPNIC_TRANSACTIONS}`;

  constructor(private http: HttpClient) {}

  uploadDocument(formData: FormData): Observable<PortalResponse> {
    return this.http.post<PortalResponse>(
      `${APP_CONSTANTS.API_BASE_URL}${APP_CONSTANTS.ENDPOINTS.GEPNIC_DOCS_UPLOAD}`,
      formData
    );
  }

  getTransactions(preTenderId?: number): Observable<IntegrationJobResponseForm[]> {
    const url = preTenderId ? `${this.baseUrl}?preTenderId=${preTenderId}` : this.baseUrl;
    return this.http.get<IntegrationJobResponseForm[]>(url);
  }

  getTransactionById(id: number): Observable<IntegrationJobResponseForm> {
    return this.http.get<IntegrationJobResponseForm>(`${this.baseUrl}/${id}`);
  }

  getTransactionAudit(id: number): Observable<AuditEventResponseForm[]> {
    return this.http.get<AuditEventResponseForm[]>(`${this.baseUrl}/${id}/audit`);
  }

  retryTransaction(id: number, forceReconcile = false): Observable<IntegrationResult> {
    return this.http.post<IntegrationResult>(
      `${this.baseUrl}/${id}/retry?forceReconcile=${forceReconcile}`,
      {}
    );
  }

  getConfiguration(): Observable<{ [key: string]: any }> {
    return this.http.get<{ [key: string]: any }>(
      `${APP_CONSTANTS.API_BASE_URL}${APP_CONSTANTS.ENDPOINTS.GEPNIC_CONFIG}`
    );
  }

  updateConfiguration(updates: any): Observable<{ [key: string]: any }> {
    return this.http.put<{ [key: string]: any }>(
      `${APP_CONSTANTS.API_BASE_URL}${APP_CONSTANTS.ENDPOINTS.GEPNIC_CONFIG}`,
      updates
    );
  }
}

