import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { APP_CONSTANTS } from '../constants/app.constants';
import { DropdownMasterForm, DropdownMasterResponseForm } from '../models/app.models';

@Injectable({
  providedIn: 'root',
})
export class DropdownService {
  private readonly baseUrl = `${APP_CONSTANTS.API_BASE_URL}${APP_CONSTANTS.ENDPOINTS.MASTERS_DROPDOWNS}`;

  constructor(private http: HttpClient) {}

  getAllGrouped(): Observable<{ [category: string]: DropdownMasterResponseForm[] }> {
    return this.http.get<{ [category: string]: DropdownMasterResponseForm[] }>(this.baseUrl);
  }

  getByCategory(category: string): Observable<DropdownMasterResponseForm[]> {
    return this.http.get<DropdownMasterResponseForm[]>(`${this.baseUrl}/${category}`);
  }

  createOption(form: DropdownMasterForm): Observable<DropdownMasterResponseForm> {
    return this.http.post<DropdownMasterResponseForm>(this.baseUrl, form);
  }

  updateOption(id: number, form: DropdownMasterForm): Observable<DropdownMasterResponseForm> {
    return this.http.put<DropdownMasterResponseForm>(`${this.baseUrl}/${id}`, form);
  }

  deleteOption(id: number): Observable<{ status: string }> {
    return this.http.delete<{ status: string }>(`${this.baseUrl}/${id}`);
  }
}

