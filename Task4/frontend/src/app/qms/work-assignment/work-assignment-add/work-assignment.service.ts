import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, shareReplay } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { Message } from '../../../common/forms/message';

export interface DropdownItem {
  id: number;
  name: string;
}

export interface WorkItem {
  id: number;
  name: string;
}

export interface WorkAllocationPayload {
  workId: number;
  srNo: number;
  approvalLevel: string;
  designationId: number;
  designationName?: string;
  officeId: number;
  officeName?: string;
  userId: number;
  userName?: string;
}

@Injectable({
  providedIn: 'root'
})
export class WorkAssignmentService {

  private readonly baseUrl = `${environment.baseurl}workassignment`;

  constructor(private readonly http: HttpClient) { }

  // 1. Get Row Count from Parameter Config Table (qms_parameter & qms_parameter_config)
  getAllocationRowCount(): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/allocation-row-count`);
  }

  // 2. Top Filter Dropdowns
  getDistricts(): Observable<DropdownItem[]> {
    return this.http.get<DropdownItem[]>(`${this.baseUrl}/districts`);
  }

  getDivisions(): Observable<DropdownItem[]> {
    return this.http.get<DropdownItem[]>(`${this.baseUrl}/divisions`);
  }

  getWorkTypes(): Observable<DropdownItem[]> {
    return this.http.get<DropdownItem[]>(`${this.baseUrl}/worktypes`);
  }

  getWorkCategories(): Observable<DropdownItem[]> {
    return this.http.get<DropdownItem[]>(`${this.baseUrl}/workcategories`);
  }

    // 3. Get Works based on division, workType, workCategory (Sent in Request Body)
  getWorks(divisionId: number, workTypeId: number, workCategoryId: number, description: string = ''): Observable<WorkItem[]> {
    const payload = {
      divisionId,
      workTypeId,
      workCategoryId,
      description
    };
    return this.http.post<WorkItem[]>(`${this.baseUrl}/works`, payload);
  }

  private designations$?: Observable<DropdownItem[]>;
  private offices$?: Observable<DropdownItem[]>;

  // 4. Allocation Dropdowns (Cached with shareReplay for instantaneous load)
  getDesignations(): Observable<DropdownItem[]> {
    if (!this.designations$) {
      this.designations$ = this.http.get<DropdownItem[]>(`${this.baseUrl}/designations`).pipe(shareReplay(1));
    }
    return this.designations$;
  }

  getOffices(): Observable<DropdownItem[]> {
    if (!this.offices$) {
      this.offices$ = this.http.get<DropdownItem[]>(`${this.baseUrl}/offices`).pipe(shareReplay(1));
    }
    return this.offices$;
  }

  getUsers(designationId: number, officeId: number): Observable<DropdownItem[]> {
    return this.http.get<DropdownItem[]>(`${this.baseUrl}/users/${designationId}/${officeId}`);
  }

  // 5. Save & Fetch Allocation (Audited by Hibernate Envers)
  saveAllocations(allocations: WorkAllocationPayload[]): Observable<Message> {
    return this.http.post<Message>(`${this.baseUrl}/save-allocation`, allocations);
  }

  getAllocationsByWorkId(workId: number): Observable<WorkAllocationPayload[]> {
    return this.http.get<WorkAllocationPayload[]>(`${this.baseUrl}/allocation/${workId}`);
  }

  // 6. Get ONLY works that are already allocated (For View screen)
  getAllocatedWorks(): Observable<DropdownItem[]> {
    return this.http.get<DropdownItem[]>(`${this.baseUrl}/allocated-works`);
  }
}
