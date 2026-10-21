import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { map, Observable } from 'rxjs';
import {
  ApiResponse,
  WorkImportPreview,
  WorkImportResult,
  WorkAuditResponse,
  WorkRequest,
  WorkReportFilters,
  WorkReportSummary,
  WorkResponse,
} from '../models/work.model';
import { environment } from '../../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class WorkService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/work`;

  getAll(): Observable<WorkResponse[]> {
    return this.http
      .get<ApiResponse<WorkResponse[]>>(this.baseUrl)
      .pipe(map((response) => response.data));
  }

  getById(id: number): Observable<WorkResponse> {
    return this.http
      .get<ApiResponse<WorkResponse>>(`${this.baseUrl}/${id}`)
      .pipe(map((response) => response.data));
  }

  getAuditHistory(id: number): Observable<WorkAuditResponse[]> {
    return this.http
      .get<ApiResponse<WorkAuditResponse[]>>(`${this.baseUrl}/${id}/audit`)
      .pipe(map((response) => response.data));
  }

  create(work: WorkRequest): Observable<WorkResponse> {
    return this.http
      .post<ApiResponse<WorkResponse>>(this.baseUrl, work)
      .pipe(map((response) => response.data));
  }

  update(id: number, work: WorkRequest): Observable<WorkResponse> {
    return this.http
      .put<ApiResponse<WorkResponse>>(`${this.baseUrl}/${id}`, work)
      .pipe(map((response) => response.data));
  }

  delete(id: number): Observable<void> {
    return this.http.delete<ApiResponse<null>>(`${this.baseUrl}/${id}`).pipe(map(() => undefined));
  }

  getReportSummary(filters: WorkReportFilters): Observable<WorkReportSummary> {
    return this.http
      .get<ApiResponse<WorkReportSummary>>(`${this.baseUrl}/reports/summary`, {
        params: this.reportParams(filters),
      })
      .pipe(map((response) => response.data));
  }

  getReportDetails(filters: WorkReportFilters): Observable<WorkResponse[]> {
    return this.http
      .get<ApiResponse<WorkResponse[]>>(`${this.baseUrl}/reports/details`, {
        params: this.reportParams(filters),
      })
      .pipe(map((response) => response.data));
  }

  downloadWorkRegister(filters: WorkReportFilters): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/reports/work-register.pdf`, {
      params: this.reportParams(filters),
      responseType: 'blob',
    });
  }

  downloadSampleExcel(): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/excel/sample`, {
      responseType: 'blob',
    });
  }

  previewExcel(file: File): Observable<WorkImportPreview> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http
      .post<ApiResponse<WorkImportPreview>>(`${this.baseUrl}/excel/preview`, formData)
      .pipe(map((response) => response.data));
  }

  importExcel(file: File): Observable<WorkImportResult> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http
      .post<ApiResponse<WorkImportResult>>(`${this.baseUrl}/excel/import`, formData)
      .pipe(map((response) => response.data));
  }

  private reportParams(filters: WorkReportFilters): HttpParams {
    let params = new HttpParams();
    if (filters.fromDate) {
      params = params.set('fromDate', filters.fromDate);
    }
    if (filters.toDate) {
      params = params.set('toDate', filters.toDate);
    }
    if (filters.contractor.trim()) {
      params = params.set('contractor', filters.contractor.trim());
    }
    return params;
  }
}
