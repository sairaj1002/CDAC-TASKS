import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { OTHERS_API, UNIT_API } from '../constants/others-constants';
import { COMMON_API } from '../../../../shared/constants/common-constants';
export interface UnitOption { id: number; shortName: string; description: string; active: boolean; }
export interface OthersRecord { id: number; description: string; unit: UnitOption; quantity: number; workId?: number; deleted: boolean; createdAt?: string; updatedAt?: string; }
export interface OthersRequest { description: string; unitId: number; quantity: number; workId?: number; }
export interface PageResponse<T> { content: T[]; totalElements: number; totalPages: number; number: number; size: number; }
@Injectable({ providedIn: 'root' })
export class OthersService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = COMMON_API.BASE_URL;
  getUnits(): Observable<UnitOption[]> { return this.http.get<UnitOption[]>(`${this.apiUrl}${UNIT_API.ENDPOINT}`); }
  getOthers(page = 0, size = 10): Observable<PageResponse<OthersRecord>> { return this.http.get<PageResponse<OthersRecord>>(`${this.apiUrl}${OTHERS_API.ENDPOINT}`, { params: { page, size } }); }
  createOther(request: OthersRequest): Observable<OthersRecord> { return this.http.post<OthersRecord>(`${this.apiUrl}${OTHERS_API.ENDPOINT}`, request); }
  updateOther(id: number, request: OthersRequest): Observable<OthersRecord> { return this.http.put<OthersRecord>(`${this.apiUrl}${OTHERS_API.ENDPOINT}/${id}`, request); }
  deleteOther(id: number): Observable<void> { return this.http.delete<void>(`${this.apiUrl}${OTHERS_API.ENDPOINT}/${id}`); }
  getAuditHistory(id: number): Observable<any[]> { return this.http.get<any[]>(`${this.apiUrl}${OTHERS_API.ENDPOINT}/${id}${OTHERS_API.AUDIT_SUBPATH}`); }
}
