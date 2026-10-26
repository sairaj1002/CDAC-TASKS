import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { catchError, map, Observable, shareReplay, tap } from 'rxjs';
import { ApiSuccessResponse } from '../../api-response';
import {
  CreateDropdownOptionRequest,
  DropdownCategory,
  DropdownOption,
  SelectOption,
  UpdateDropdownOptionRequest,
} from '../dropdown-admin/dropdown-option';
import { DROPDOWN_ADMIN_CONSTANTS } from '../../constant/constant';

@Injectable({ providedIn: 'root' })
export class DropdownService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = DROPDOWN_ADMIN_CONSTANTS.API_URL;

  // In-memory cache keyed by canonical category string
  private readonly cache = new Map<string, Observable<DropdownOption[]>>();

  constructor() {
    this.preInit();
  }

  /**
   * Pre-fetches options for high-frequency categories on application start.
   */
  preInit(): void {
    for (const cat of DROPDOWN_ADMIN_CONSTANTS.DEFAULT_PREINIT_CATEGORIES) {
      this.getOptions(cat as DropdownCategory).subscribe({
        error: (err) => console.warn(`[DropdownService] Pre-init warning for ${cat}:`, err),
      });
    }
  }

  /**
   * Canonical category normalizer.
   */
  normalizeCategory(category: string): string {
    if (!category) return '';
    const clean = category.trim().toUpperCase().replace(/-/g, '_');
    if (clean === 'PROPOSED_ROAD_TYPE' || clean === 'PROPOSED_ROAD_TYPES' || clean === 'ROAD_TYPES') {
      return 'ROAD_TYPE';
    }
    if (clean === 'NATURE_OF_WORKS') return 'NATURE_OF_WORK';
    if (clean === 'WORK_PLAN_TYPES') return 'WORK_PLAN_TYPE';
    if (clean === 'CD_WORK_TYPES') return 'CD_WORK_TYPE';
    if (clean === 'REPAIR_MAINTENANCE_TYPES') return 'REPAIR_MAINTENANCE_TYPE';
    if (clean === 'BRIDGE_ACTIVITIES' || clean === 'ACTIVITIES') return 'BRIDGE_ACTIVITY';
    return clean;
  }

  /**
   * Fetch active dropdown options for a category with in-memory shareReplay(1) caching.
   */
  getOptions(category: DropdownCategory, forceRefresh = false): Observable<DropdownOption[]> {
    const canonical = this.normalizeCategory(category);

    if (forceRefresh || !this.cache.has(canonical)) {
      const request$ = this.http
        .get<ApiSuccessResponse<DropdownOption[]> | DropdownOption[]>(`${this.apiUrl}/${canonical}`)
        .pipe(
          map((res) => {
            const list = Array.isArray(res) ? res : res?.data ?? [];
            return [...list].sort((a, b) => a.displayOrder - b.displayOrder || a.id - b.id);
          }),
          shareReplay(1),
          catchError((err) => {
            this.cache.delete(canonical);
            throw err;
          }),
        );

      this.cache.set(canonical, request$);
    }

    return this.cache.get(canonical)!;
  }

  /**
   * Fetch options mapped directly to standard { label, value } pairs (value = code).
   */
  getSelectOptions(category: DropdownCategory, forceRefresh = false): Observable<SelectOption[]> {
    return this.getOptions(category, forceRefresh).pipe(
      map((options) =>
        options.map((opt) => ({
          label: opt.label,
          value: opt.code,
        })),
      ),
    );
  }

  /**
   * Fetch all dropdown options (active & inactive), optionally filtered by category.
   * Used for administration.
   */
  getAll(category?: string): Observable<DropdownOption[]> {
    const url = category && category !== 'ALL'
      ? `${this.apiUrl}?category=${encodeURIComponent(this.normalizeCategory(category))}`
      : this.apiUrl;

    return this.http.get<ApiSuccessResponse<DropdownOption[]> | DropdownOption[]>(url).pipe(
      map((res) => {
        const list = Array.isArray(res) ? res : res?.data ?? [];
        return [...list].sort((a, b) => {
          if (a.category !== b.category) {
            return a.category.localeCompare(b.category);
          }
          return a.displayOrder - b.displayOrder || a.id - b.id;
        });
      }),
    );
  }

  /**
   * Fetch distinct category names from database.
   */
  getCategories(): Observable<string[]> {
    return this.http
      .get<ApiSuccessResponse<string[]> | string[]>(`${this.apiUrl}${DROPDOWN_ADMIN_CONSTANTS.CATEGORIES_ENDPOINT}`)
      .pipe(map((res) => (Array.isArray(res) ? res : res?.data ?? [])));
  }

  /**
   * Create a new dropdown option and invalidate relevant cache.
   */
  create(payload: CreateDropdownOptionRequest): Observable<DropdownOption> {
    return this.http
      .post<ApiSuccessResponse<DropdownOption>>(`${this.apiUrl}`, payload)
      .pipe(
        map((res) => res.data),
        tap((created) => this.refreshCategory(created.category)),
      );
  }

  /**
   * Update an existing dropdown option and invalidate relevant cache.
   */
  update(id: number, payload: UpdateDropdownOptionRequest, category?: string): Observable<DropdownOption> {
    return this.http
      .put<ApiSuccessResponse<DropdownOption>>(`${this.apiUrl}/${id}`, payload)
      .pipe(
        map((res) => res.data),
        tap((updated) => this.refreshCategory(category || updated.category)),
      );
  }

  /**
   * Toggle active/inactive status and invalidate cache.
   */
  toggleActive(id: number, active: boolean, category?: string): Observable<DropdownOption> {
    return this.http
      .patch<ApiSuccessResponse<DropdownOption>>(`${this.apiUrl}/${id}/status?active=${active}`, {})
      .pipe(
        map((res) => res.data),
        tap((updated) => this.refreshCategory(category || updated.category)),
      );
  }

  /**
   * Delete an option and invalidate cache.
   */
  delete(id: number, category?: string): Observable<void> {
    return this.http
      .delete<ApiSuccessResponse<void>>(`${this.apiUrl}/${id}`)
      .pipe(
        map(() => undefined),
        tap(() => {
          if (category) {
            this.refreshCategory(category);
          } else {
            this.clearCache();
          }
        }),
      );
  }

  /**
   * Invalidate cache for a specific category.
   */
  refreshCategory(category: string): void {
    const canonical = this.normalizeCategory(category);
    this.cache.delete(canonical);
  }

  /**
   * Clear all cached categories.
   */
  clearCache(): void {
    this.cache.clear();
  }
}
