import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { map, Observable } from 'rxjs';

import { environment } from '../../../../environments/environment';
import {
  ApiResponse,
  WorkDetailsRequest,
  WorkDetailsResponse,
} from '../models/scopes.model';

@Injectable({ providedIn: 'root' })
export class ScopesService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/scopes/work-details`;

  getAll(): Observable<WorkDetailsResponse[]> {
    return this.http
      .get<ApiResponse<WorkDetailsResponse[]>>(this.baseUrl)
      .pipe(map((response) => response.data));
  }

  create(request: WorkDetailsRequest): Observable<WorkDetailsResponse> {
    return this.http
      .post<ApiResponse<WorkDetailsResponse>>(this.baseUrl, request)
      .pipe(map((response) => response.data));
  }

  update(workId: number, request: WorkDetailsRequest): Observable<WorkDetailsResponse> {
    return this.http
      .put<ApiResponse<WorkDetailsResponse>>(`${this.baseUrl}/${workId}`, request)
      .pipe(map((response) => response.data));
  }

  delete(workId: number): Observable<void> {
    return this.http
      .delete<ApiResponse<null>>(`${this.baseUrl}/${workId}`)
      .pipe(map(() => undefined));
  }
}
