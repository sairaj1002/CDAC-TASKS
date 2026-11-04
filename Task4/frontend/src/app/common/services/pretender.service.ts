import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { APP_CONSTANTS } from '../constants/app.constants';
import { IntegrationResult, PreTenderDocumentResponse, PreTenderForm, PreTenderResponseForm } from '../models/app.models';

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

  /** The documents of a saved tender, with whether each file is stored. */
  getDocuments(id: number): Observable<PreTenderDocumentResponse[]> {
    return this.http.get<PreTenderDocumentResponse[]>(`${this.baseUrl}/${id}/documents`);
  }

  /** Stores a file as a document of the tender, under a DOC_CODE category. */
  uploadDocument(id: number, docCode: string, file: File): Observable<PreTenderDocumentResponse> {
    const body = new FormData();
    body.append('docCode', docCode);
    body.append('file', file, file.name);
    return this.http.post<PreTenderDocumentResponse>(`${this.baseUrl}/${id}/documents`, body);
  }

  downloadDocument(id: number, documentId: number): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/${id}/documents/${documentId}/file`, { responseType: 'blob' });
  }

  deleteDocument(id: number, documentId: number): Observable<{ status: string }> {
    return this.http.delete<{ status: string }>(`${this.baseUrl}/${id}/documents/${documentId}`);
  }

  /** The tender JSON exactly as it would be sent to GePNIC (for viewing; nothing is sent). */
  getGePNICJson(id: number): Observable<unknown> {
    return this.http.get<unknown>(`${this.baseUrl}/${id}/gepnic-json`);
  }

  submitToGepnic(id: number): Observable<IntegrationResult> {
    return this.http.post<IntegrationResult>(`${this.baseUrl}/${id}/integration-jobs?portalCode=GEPNIC`, {});
  }
}

