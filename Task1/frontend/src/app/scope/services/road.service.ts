import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { ApiSuccessResponse } from '../../api-response';
import { DropdownCategory, DropdownOption } from '../dropdown-admin/dropdown-option';
import { DropdownService } from './dropdown.service';
import { ROAD_API, ROAD_CATEGORIES } from '../../constant/roadconstant';

export interface AuditDetail {
  remark: string | null;
  name: string | null;
  employeeName: string | null;
  designation: string | null;
  timestamp: string | Date | null;
}

export interface RoadModel {
  scopeId: number;
  id: number;
  natureOfWork: string;
  proposedRoadType: string;
  length: number | null;
  width: number | null;
  workPlanType: string;
  cdWorkType: string;
  repairMaintenanceType: string | null;
  officeId: number | null;
  workId: number | null;
  tenantId?: number | null;
  createdAt: string | null;
  updatedAt: string | null;
  revisionTimestamp: string | null;
  auditDetails?: AuditDetail[] | null;
}

export type RoadItem = RoadModel;
export type RoadComponentItem = RoadModel;

export interface RoadComponentFormValue {
  natureOfWork: string;
  proposedRoadType: string;
  length: number | null;
  width: number | null;
  workPlanType: string;
  cdWorkType: string;
  repairMaintenanceType: string | null;
}

export type RoadComponentRequest = RoadComponentFormValue & {
  officeId: number | null;
  workId: number | null;
};

export interface RoadEnumOption {
  label: string;
  value: string;
}

export interface RoadAuditRevision {
  revisionNumber: number;
  revisionDate: string;
  revisionType: 'CREATED' | 'UPDATED' | 'DELETED';
  snapshot: RoadModel;
}

@Injectable({ providedIn: 'root' })
export class RoadService {
  private readonly http = inject(HttpClient);
  private readonly dropdownOptionService = inject(DropdownService);
  private readonly apiUrl = ROAD_API.BASE_URL;

  getComponents(): Observable<RoadModel[]> {
    return this.unwrap(
      this.http.get<ApiSuccessResponse<RoadModel[]>>(`${this.apiUrl}${ROAD_API.ENDPOINT}`),
    );
  }

  getNatureOfWorks(): Observable<RoadEnumOption[]> {
    return this.getOptionsByCategory(ROAD_CATEGORIES.NATURE_OF_WORK);
  }

  getProposedRoadTypes(): Observable<RoadEnumOption[]> {
    return this.getOptionsByCategory(ROAD_CATEGORIES.ROAD_TYPE);
  }

  getCdWorkTypes(): Observable<RoadEnumOption[]> {
    return this.getOptionsByCategory(ROAD_CATEGORIES.CD_WORK_TYPE);
  }

  getWorkPlanTypes(): Observable<RoadEnumOption[]> {
    return this.getOptionsByCategory(ROAD_CATEGORIES.WORK_PLAN_TYPE);
  }

  getRepairMaintenanceTypes(): Observable<RoadEnumOption[]> {
    return this.getOptionsByCategory(ROAD_CATEGORIES.REPAIR_MAINTENANCE_TYPE);
  }

  getRawOptions(category: DropdownCategory): Observable<DropdownOption[]> {
    return this.dropdownOptionService.getOptions(category);
  }

  createComponent(component: RoadComponentRequest): Observable<RoadModel> {
    return this.unwrap(
      this.http.post<ApiSuccessResponse<RoadModel>>(`${this.apiUrl}${ROAD_API.ENDPOINT}`, component),
    );
  }

  updateComponent(id: number, component: RoadComponentRequest): Observable<RoadModel> {
    return this.unwrap(
      this.http.put<ApiSuccessResponse<RoadModel>>(
        `${this.apiUrl}${ROAD_API.ENDPOINT}/${id}`,
        component,
      ),
    );
  }

  deleteComponent(id: number): Observable<void> {
    return this.http
      .delete<ApiSuccessResponse<null>>(`${this.apiUrl}${ROAD_API.ENDPOINT}/${id}`)
      .pipe(map(() => undefined));
  }

  getAuditHistory(id: number): Observable<RoadAuditRevision[]> {
    return this.unwrap(
      this.http.get<ApiSuccessResponse<RoadAuditRevision[]>>(
        `${this.apiUrl}${ROAD_API.ENDPOINT}/${id}${ROAD_API.AUDIT_SUBPATH}`,
      ),
    );
  }

  private getOptionsByCategory(category: DropdownCategory): Observable<RoadEnumOption[]> {
    return this.dropdownOptionService.getOptions(category).pipe(
      map((options) =>
        options.map((opt) => ({
          label: opt.label,
          value: opt.code,
        })),
      ),
    );
  }

  private unwrap<T>(request: Observable<ApiSuccessResponse<T>>): Observable<T> {
    return request.pipe(map(({ data }) => data));
  }
}

export { RoadService as RoadApiService };
