import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { ApiSuccessResponse } from '../../api-response';
import { DropdownOption } from '../dropdown-admin/dropdown-option';
import { DropdownService } from './dropdown.service';
import { BRIDGE_API, BRIDGE_CATEGORIES } from '../../constant/bridgeconstant';

export interface AuditDetail {
  remark: string | null;
  name: string | null;
  employeeName: string | null;
  designation: string | null;
  timestamp: string | Date | null;
}

export interface BridgeModel {
  scopeId: number;
  id: number;
  riverNallahBarrier: string;
  width: number | null;
  length: number | null;
  approvalRoad: string;
  numberOfSpans: number | null;
  spanLength: number | null;
  bridgeActivity: string;
  otherActivityDescription: string;
  officeId: number | null;
  workId: number | null;
  tenantId?: number | null;
  createdAt: string | null;
  updatedAt: string | null;
  revisionTimestamp: string | null;
  auditDetails?: AuditDetail[] | null;
}

export type BridgeItem = BridgeModel;
export type BridgeComponentItem = BridgeModel;

export type BridgeComponentFormValue = Omit<
  BridgeModel,
  'scopeId' | 'id' | 'officeId' | 'workId' | 'tenantId' | 'createdAt' | 'updatedAt' | 'revisionTimestamp' | 'auditDetails'
>;

export type BridgeComponentRequest = BridgeComponentFormValue &
  Pick<BridgeModel, 'officeId' | 'workId'>;

export interface BridgeAuditRevision {
  revisionNumber: number;
  revisionDate: string;
  revisionType: 'CREATED' | 'UPDATED' | 'DELETED';
  snapshot: BridgeModel;
}

@Injectable({ providedIn: 'root' })
export class BridgeService {
  private readonly http = inject(HttpClient);
  private readonly dropdownOptionService = inject(DropdownService);
  private readonly apiUrl = BRIDGE_API.BASE_URL;

  getComponents(): Observable<BridgeModel[]> {
    return this.unwrap(
      this.http.get<ApiSuccessResponse<BridgeModel[]>>(`${this.apiUrl}${BRIDGE_API.ENDPOINT}`),
    );
  }

  getBridgeActivities(): Observable<string[]> {
    return this.dropdownOptionService.getOptions(BRIDGE_CATEGORIES.BRIDGE_ACTIVITY).pipe(
      map((options) => options.map((opt) => opt.label)),
    );
  }

  getBridgeActivityOptions(): Observable<DropdownOption[]> {
    return this.dropdownOptionService.getOptions(BRIDGE_CATEGORIES.BRIDGE_ACTIVITY);
  }

  createComponent(component: BridgeComponentRequest): Observable<BridgeModel> {
    return this.unwrap(
      this.http.post<ApiSuccessResponse<BridgeModel>>(
        `${this.apiUrl}${BRIDGE_API.ENDPOINT}`,
        component,
      ),
    );
  }

  updateComponent(id: number, component: BridgeComponentRequest): Observable<BridgeModel> {
    return this.unwrap(
      this.http.put<ApiSuccessResponse<BridgeModel>>(
        `${this.apiUrl}${BRIDGE_API.ENDPOINT}/${id}`,
        component,
      ),
    );
  }

  deleteComponent(id: number): Observable<void> {
    return this.http
      .delete<ApiSuccessResponse<null>>(`${this.apiUrl}${BRIDGE_API.ENDPOINT}/${id}`)
      .pipe(map(() => undefined));
  }

  getAuditHistory(id: number): Observable<BridgeAuditRevision[]> {
    return this.unwrap(
      this.http.get<ApiSuccessResponse<BridgeAuditRevision[]>>(
        `${this.apiUrl}${BRIDGE_API.ENDPOINT}/${id}${BRIDGE_API.AUDIT_SUBPATH}`,
      ),
    );
  }

  private unwrap<T>(request: Observable<ApiSuccessResponse<T>>): Observable<T> {
    return request.pipe(map(({ data }) => data));
  }
}

export { BridgeService as BridgeApiService };
