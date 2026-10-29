import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpService } from '../../../core/services/http.service';
import { GEPNIC_CONSTANTS } from '../constants/gepnic.constants';
import { ApiResponse, DocumentTransaction } from '../models/gepnic.models';

@Injectable({
  providedIn: 'root'
})
export class DocumentService {

  private baseUrl = `${GEPNIC_CONSTANTS.BACKEND_API_BASE}/documents`;

  constructor(private http: HttpService) {}

  uploadDocument(form: any, file?: File): Observable<ApiResponse<any>> {
    const formData = new FormData();
    formData.append('form', new Blob([JSON.stringify(form)], { type: 'application/json' }));
    if (file) {
      formData.append('file', file, file.name);
    }
    return this.http.postMultipart<ApiResponse<any>>(`${this.baseUrl}/upload`, formData);
  }

  checkDocumentStatus(form: any): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(`${this.baseUrl}/status`, form);
  }

  getTenderDocuments(tenderRef: string): Observable<ApiResponse<DocumentTransaction[]>> {
    return this.http.get<ApiResponse<DocumentTransaction[]>>(`${this.baseUrl}/tender/${tenderRef}`);
  }
}
