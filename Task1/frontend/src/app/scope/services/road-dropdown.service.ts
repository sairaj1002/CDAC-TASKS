import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { DropdownCategory, DropdownOption } from '../dropdown-admin/dropdown-option';
import { DropdownService } from './dropdown.service';
import { ROAD_CATEGORIES } from '../../constant/roadconstant';

/**
 * Backward-compatible wrapper delegating to the centralized DropdownService.
 */
@Injectable({ providedIn: 'root' })
export class RoadDropdownService {
  private readonly dropdownService = inject(DropdownService);

  getOptions(category: DropdownCategory, forceRefresh = true): Observable<DropdownOption[]> {
    return this.dropdownService.getOptions(category, forceRefresh);
  }

  getNatureOfWorks(forceRefresh = false): Observable<DropdownOption[]> {
    return this.getOptions(ROAD_CATEGORIES.NATURE_OF_WORK, forceRefresh);
  }

  getProposedRoadTypes(forceRefresh = false): Observable<DropdownOption[]> {
    return this.getOptions(ROAD_CATEGORIES.ROAD_TYPE, forceRefresh);
  }

  getWorkPlanTypes(forceRefresh = false): Observable<DropdownOption[]> {
    return this.getOptions(ROAD_CATEGORIES.WORK_PLAN_TYPE, forceRefresh);
  }

  getCdWorkTypes(forceRefresh = false): Observable<DropdownOption[]> {
    return this.getOptions(ROAD_CATEGORIES.CD_WORK_TYPE, forceRefresh);
  }

  getRepairMaintenanceTypes(forceRefresh = false): Observable<DropdownOption[]> {
    return this.getOptions(ROAD_CATEGORIES.REPAIR_MAINTENANCE_TYPE, forceRefresh);
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
