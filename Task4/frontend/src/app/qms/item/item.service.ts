import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Message } from '../../common/forms/message';

export interface DropdownOption {
  id: number;
  name: string;
}

export interface Item {
  itemId?: number;
  itemCode: string;
  itemDescription: string;
  workTypeId: number;
  workTypeName?: string;
  status?: string;
  itemPurpose: string;
}

// One line of the Audit Details pop-up; timestamp arrives already formatted (dd/MM/yyyy HH:mm:ss)
export interface ItemAudit {
  remark: string;
  name: string;
  employeeName: string;
  designation: string;
  timestamp: string;
}

// One page of items and the total number of items that match the search
export interface ItemPage {
  count: number;
  data: Item[];
}

@Injectable({ providedIn: 'root' })
export class ItemService {

  private readonly baseUrl = `${environment.baseurl}item`;

  constructor(private readonly http: HttpClient) { }

  getWorkTypes(): Observable<DropdownOption[]> {
    return this.http.get<DropdownOption[]>(`${this.baseUrl}/worktypes`);
  }

  // All request data goes in the request body; nothing is put in the URL (no query parameters, no path variables).
  //
  // Both filters are optional: itemCode matches part of the code, status is REGISTERED / APPROVED / CANCELLED.
  // pageIndex starts at 0. The answer holds that page (data) and the total number of matches (count).
  getItems(itemCode = '', status = '', pageIndex = 0, pageSize = 10): Observable<ItemPage> {
    const body = { itemCode: itemCode ?? '', status: status ?? '', pageIndex, pageSize };
    return this.http.post<ItemPage>(`${this.baseUrl}/list`, body);
  }

  // save, update, approve and delete answer with the Message to show (text and style come from the backend)
  saveItem(item: Item): Observable<Message> {
    return this.http.post<Message>(`${this.baseUrl}/save`, item);
  }

  // The item to change is identified by itemId inside the body
  updateItem(itemId: number, item: Item): Observable<Message> {
    return this.http.post<Message>(`${this.baseUrl}/update`, { ...item, itemId });
  }

  approveItem(itemId: number): Observable<Message> {
    return this.http.post<Message>(`${this.baseUrl}/approve`, { itemId });
  }

  deleteItem(itemId: number): Observable<Message> {
    return this.http.post<Message>(`${this.baseUrl}/delete`, { itemId });
  }

  // History of one item, oldest change first
  getAudit(itemId: number): Observable<ItemAudit[]> {
    return this.http.post<ItemAudit[]>(`${this.baseUrl}/audit`, { itemId });
  }
}