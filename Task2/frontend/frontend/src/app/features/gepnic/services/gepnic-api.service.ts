import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpService } from '../../../core/services/http.service';
import { GEPNIC_CONSTANTS } from '../constants/gepnic.constants';
import { ApiResponse, OfficeItem, ConfigurationView } from '../models/gepnic.models';

@Injectable({
  providedIn: 'root'
})
export class GepnicApiService {

  private baseUrl = GEPNIC_CONSTANTS.BACKEND_API_BASE;

  constructor(private http: HttpService) {}

  getPermittedOffices(search?: string): Observable<ApiResponse<OfficeItem[]>> {
    const params: Record<string, string> = {};
    if (search) params['search'] = search;
    return this.http.get<ApiResponse<OfficeItem[]>>(`${this.baseUrl}/offices`, params);
  }

  getConfiguration(): Observable<ApiResponse<ConfigurationView>> {
    return this.http.get<ApiResponse<ConfigurationView>>(`${this.baseUrl}/configuration`);
  }

  updateConfiguration(config: any): Observable<ApiResponse<any>> {
    return this.http.put<ApiResponse<any>>(`${this.baseUrl}/configuration`, config);
  }
}
