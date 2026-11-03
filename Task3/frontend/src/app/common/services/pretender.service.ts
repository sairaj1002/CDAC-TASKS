import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { APP_CONSTANTS } from '../constants/app.constants';
import { IntegrationResult, PreTenderForm, PreTenderResponseForm } from '../models/app.models';

@Injectable({
  providedIn: 'root',
})
export class PreTenderService {
  private readonly baseUrl = `${APP_CONSTANTS.API_BASE_URL}${APP_CONSTANTS.ENDPOINTS.PRE_TENDERS}`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<PreTenderResponseForm[]> {
    return this.http.get<PreTenderResponseForm[]>(this.baseUrl);
  }

  getById(id: number): Observable<PreTenderResponseForm> {
    return this.http.get<PreTenderResponseForm>(`${this.baseUrl}/${id}`);
  }

  create(form: PreTenderForm): Observable<PreTenderResponseForm> {
    return this.http.post<PreTenderResponseForm>(this.baseUrl, form);
  }

  update(id: number, form: PreTenderForm): Observable<PreTenderResponseForm> {
    return this.http.put<PreTenderResponseForm>(`${this.baseUrl}/${id}`, form);
  }

  delete(id: number): Observable<{ status: string }> {
    return this.http.delete<{ status: string }>(`${this.baseUrl}/${id}`);
  }

  validate(id: number): Observable<{ status: string }> {
    return this.http.post<{ status: string }>(`${this.baseUrl}/${id}/validate`, {});
  }

  submitToGepnic(id: number): Observable<IntegrationResult> {
    return this.http.post<IntegrationResult>(`${this.baseUrl}/${id}/integration-jobs?portalCode=GEPNIC`, {});
  }
}

