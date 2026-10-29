import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MasterOption, MasterOptionDraft, TenderDraft, TenderRecord } from '../models/pretender.model';
import { WorkSummary } from '../models/work-summary.model';

export type TenderUploadFields = Partial<Record<'nitFileName' | 'dtcnFileName' | 'otherFileName', File>>;

@Injectable({ providedIn: 'root' })
export class PretenderApiService {
  private readonly api = 'http://localhost:8080/api';

  constructor(private readonly http: HttpClient) {}

  listMasters(key: string): Observable<MasterOption[]> {
    return this.http.get<MasterOption[]>(`${this.api}/masters/${key}`);
  }

  createMaster(key: string, value: MasterOptionDraft): Observable<MasterOption> {
    return this.http.post<MasterOption>(`${this.api}/masters/${key}`, value);
  }

  updateMaster(key: string, id: number, value: MasterOptionDraft): Observable<MasterOption> {
    return this.http.put<MasterOption>(`${this.api}/masters/${key}/${id}`, value);
  }

  deleteMaster(key: string, id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/masters/${key}/${id}`);
  }

  uploadFile(file: File): Observable<{ fileName: string; originalName: string }> {
    const data = new FormData();
    data.append('file', file);
    return this.http.post<{ fileName: string; originalName: string }>(`${this.api}/files`, data);
  }

  saveTender(tender: TenderDraft): Observable<TenderRecord> {
    return this.http.post<TenderRecord>(`${this.api}/tenders`, tender);
  }

  updateTender(id: number, tender: TenderDraft): Observable<TenderRecord> {
    return this.http.put<TenderRecord>(`${this.api}/tenders/${id}`, tender);
  }

  getTender(id: number): Observable<TenderRecord> {
    return this.http.get<TenderRecord>(`${this.api}/tenders/${id}`);
  }

  listTenders(): Observable<TenderRecord[]> {
    return this.http.get<TenderRecord[]>(`${this.api}/tenders`);
  }

  saveTenderWithFiles(tender: TenderDraft, files: TenderUploadFields): Observable<TenderRecord> {
    return this.http.post<TenderRecord>(`${this.api}/tenders/with-files`, this.tenderFormData(tender, files));
  }

  updateTenderWithFiles(id: number, tender: TenderDraft, files: TenderUploadFields): Observable<TenderRecord> {
    return this.http.put<TenderRecord>(`${this.api}/tenders/${id}/with-files`, this.tenderFormData(tender, files));
  }

  searchWorks(query: string): Observable<WorkSummary[]> {
    const params = new HttpParams().set('q', query);
    return this.http.get<WorkSummary[]>(`${this.api}/works/search`, { params });
  }

  listWorks(): Observable<WorkSummary[]> {
    return this.http.get<WorkSummary[]>(`${this.api}/works`);
  }

  getWork(id: string): Observable<WorkSummary> {
    return this.http.get<WorkSummary>(`${this.api}/works/${encodeURIComponent(id)}`);
  }

  fileUrl(fileName: string): string {
    return `${this.api}/files/${encodeURIComponent(fileName)}`;
  }

  private tenderFormData(tender: TenderDraft, files: TenderUploadFields): FormData {
    const data = new FormData();
    data.append('tender', new Blob([JSON.stringify(tender)], { type: 'application/json' }));
    if (files.nitFileName) data.append('nitFile', files.nitFileName);
    if (files.dtcnFileName) data.append('dtcnFile', files.dtcnFileName);
    if (files.otherFileName) data.append('otherFile', files.otherFileName);
    return data;
  }
}

