import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { DropdownCategory, DropdownOption } from '../dropdown-admin/dropdown-option';
import { DropdownService } from './dropdown.service';
import { BRIDGE_CATEGORIES } from '../../constant/bridgeconstant';

/**
 * Backward-compatible wrapper delegating to the centralized DropdownService.
 */
@Injectable({ providedIn: 'root' })
export class BridgeDropdownService {
  private readonly dropdownService = inject(DropdownService);

  getOptions(category: DropdownCategory, forceRefresh = false): Observable<DropdownOption[]> {
    return this.dropdownService.getOptions(category, forceRefresh);
  }

  getBridgeActivities(forceRefresh = false): Observable<DropdownOption[]> {
    return this.getOptions(BRIDGE_CATEGORIES.BRIDGE_ACTIVITY, forceRefresh);
  }

  getAllOptions(category: DropdownCategory): Observable<DropdownOption[]> {
    return this.dropdownService.getAll(category);
  }

  refreshCategory(category: string): void {
    this.dropdownService.refreshCategory(category);
  }

  clearCache(): void {
    this.dropdownService.clearCache();
  }
}
