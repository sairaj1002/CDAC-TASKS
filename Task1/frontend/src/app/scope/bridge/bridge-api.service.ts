import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { ApiSuccessResponse } from '../../api-response';
import {
  BridgeAuditRevision,
  BridgeComponent,
  BridgeComponentRequest,
} from './bridge-component';

@Injectable({ providedIn: 'root' })
export class BridgeApiService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/api';

  getComponents(): Observable<BridgeComponent[]> {
    return this.unwrap(
      this.http.get<ApiSuccessResponse<BridgeComponent[]>>(`${this.apiUrl}/bridge-components`),
    );
  }

  getBridgeActivities(): Observable<string[]> {
    return this.unwrap(
      this.http.get<ApiSuccessResponse<string[]>>(`${this.apiUrl}/lookups/bridge-activities`),
    );
  }

  createComponent(component: BridgeComponentRequest): Observable<BridgeComponent> {
    return this.unwrap(
      this.http.post<ApiSuccessResponse<BridgeComponent>>(
        `${this.apiUrl}/bridge-components`,
        component,
      ),
    );
  }

  updateComponent(id: number, component: BridgeComponentRequest): Observable<BridgeComponent> {
    return this.unwrap(
      this.http.put<ApiSuccessResponse<BridgeComponent>>(
        `${this.apiUrl}/bridge-components/${id}`,
        component,
      ),
    );
  }

  deleteComponent(id: number): Observable<void> {
    return this.http
      .delete<ApiSuccessResponse<null>>(`${this.apiUrl}/bridge-components/${id}`)
      .pipe(map(() => undefined));
  }

  getAuditHistory(id: number): Observable<BridgeAuditRevision[]> {
    return this.unwrap(
      this.http.get<ApiSuccessResponse<BridgeAuditRevision[]>>(
        `${this.apiUrl}/bridge-components/${id}/audit`,
      ),
    );
  }

  private unwrap<T>(request: Observable<ApiSuccessResponse<T>>): Observable<T> {
    return request.pipe(map(({ data }) => data));
  }
}
