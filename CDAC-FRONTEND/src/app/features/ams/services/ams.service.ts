import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { AmsRequest, AmsResponse } from '../models/ams.model';

import { environment } from '../../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AmsService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/ams`;

  getAll(): Observable<AmsResponse[]> {
    return this.http.get<AmsResponse[]>(this.baseUrl);
  }

  getById(id: number): Observable<AmsResponse> {
    return this.http.get<AmsResponse>(`${this.baseUrl}/${id}`);
  }

  create(asset: AmsRequest): Observable<AmsResponse> {
    return this.http.post<AmsResponse>(this.baseUrl, asset);
  }

  update(id: number, asset: AmsRequest): Observable<AmsResponse> {
    return this.http.put<AmsResponse>(`${this.baseUrl}/${id}`, asset);
  }

  delete(id: number): Observable<string> {
    return this.http.delete(`${this.baseUrl}/${id}`, { responseType: 'text' });
  }
}
