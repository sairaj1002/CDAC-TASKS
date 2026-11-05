import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, map } from 'rxjs';
import { AssignedWork, GradeItem, WorkInspectionDetails } from './work-inspection.model';
import { environment } from '../../../../environments/environment';
import { Message } from '../../../common/forms/message';

export interface InspectionSavePayload {
  workId: number;
  workNumber: string;
  overallGrade: string;
  remarks?: string;
  items: {
    itemId: number;
    itemNo: number;
    awardedGrade: string;
    remarks?: string;
  }[];
}

@Injectable({
  providedIn: 'root'
})
export class WorkInspectionService {

  private readonly baseUrl = `${environment.baseurl}workinspection`;

  constructor(private readonly http: HttpClient) {}

  /**
   * Fetches all works that have been allocated in the Work Assignment screen.
   */
  getAssignedWorks(): Observable<AssignedWork[]> {
    return this.http.post<AssignedWork[]>(`${this.baseUrl}/assigned-works`, {});
  }

  /**
   * Fetches master checklist items for a given work type ('Roads', 'Bridges', 'Buildings')
   * using @RequestBody.
   */
  getGradeItems(workType: string): Observable<GradeItem[]> {
    return this.http.post<GradeItem[]>(`${this.baseUrl}/grade-items`, { workType });
  }

  /**
   * Saves the inspection header and each individual checklist item's grade into the database.
   */
  saveInspection(payload: InspectionSavePayload): Observable<Message> {
    return this.http.post<Message>(`${this.baseUrl}/save`, payload);
  }

  /**
   * Fetches all works that have already been graded (for View page dropdown).
   * Gracefully falls back to client filtering of assigned works if backend is pending restart.
   */
  getGradedWorks(): Observable<AssignedWork[]> {
    return this.http.post<AssignedWork[]>(`${this.baseUrl}/graded-works`, {}).pipe(
      catchError(() => {
        return this.getAssignedWorks().pipe(
          map(works => (works || []).filter(w => w.currentGrade && w.currentGrade !== 'Not Graded'))
        );
      })
    );
  }

  /**
   * Fetches the latest inspection details and individual item grades for a work using @RequestBody.
   */
  getInspectionDetails(workId: number): Observable<WorkInspectionDetails> {
    return this.http.post<WorkInspectionDetails>(`${this.baseUrl}/inspection-details`, { workId });
  }
}
