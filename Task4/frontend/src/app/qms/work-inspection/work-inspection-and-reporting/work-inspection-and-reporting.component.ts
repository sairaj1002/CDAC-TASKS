import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';
import {
  AssignedWork,
  GradeItem,
  GradeValue,
  calculateOverallGrade
} from './work-inspection.model';
import { WorkInspectionService, InspectionSavePayload } from './work-inspection.service';
import { SnackbarService } from '../../../common/services/snackbar.service';

@Component({
  selector: 'app-work-inspection-and-reporting',
  standalone: false,
  templateUrl: './work-inspection-and-reporting.component.html',
})
export class WorkInspectionAndReportingComponent implements OnInit {

  // Dynamic list of assigned works fetched from database
  assignedWorks: AssignedWork[] = [];

  // Selected work
  selectedWork: AssignedWork | null = null;

  // Dynamic checklist items fetched from database for selected work's workType
  gradeItems: GradeItem[] = [];

  // Calculation state
  isGradeCalculated = false;
  calculatedGradeResult = '';
  isLoading = false;

  constructor(
    private readonly router: Router,
    private readonly workInspectionService: WorkInspectionService,
    private readonly cdr: ChangeDetectorRef,
    private readonly snackbar: SnackbarService
  ) {}

  ngOnInit(): void {
    this.loadAssignedWorks();
  }

  /**
   * Helper method to remove prefixes like "Stage 1:", "Stage 2 :", "Item 1:", etc.
   * from checklist observation descriptions so only the clean name is shown.
   */
  cleanDescription(text: string): string {
    if (!text) {
      return '';
    }
    return text.replace(/^(Stage\s*\d+\s*[:\-–]?\s*|Item\s*(?:No\.?\s*)?\d+\s*[:\-–]?\s*)/i, '').trim();
  }

  /**
   * Flexible category resolver:
   * Department work types can have diverse names (e.g. 'Roads & Highways', 'State Highway', 'Major Bridges & Flyovers').
   * - Contains 'road' or 'highway' -> maps to 'Roads' checklist items
   * - Contains 'bridge' or 'flyover' or 'rob' -> maps to 'Bridges' checklist items
   * - Contains 'building' or 'bld' or 'structure' -> maps to 'Buildings' checklist items
   */
  resolveWorkTypeCategory(workType: string | null | undefined): string {
    if (!workType) {
      return 'Roads';
    }
    const lower = workType.toLowerCase().trim();
    if (lower.includes('road') || lower.includes('highway') || lower.includes('street') || lower.includes('path') || lower.includes('expressway')) {
      return 'Roads';
    }
    if (lower.includes('bridge') || lower.includes('flyover') || lower.includes('rob') || lower.includes('viaduct')) {
      return 'Bridges';
    }
    if (lower.includes('build') || lower.includes('bld') || lower.includes('structure') || lower.includes('house') || lower.includes('hospital')) {
      return 'Buildings';
    }
    return 'Roads';
  }

  /**
   * Fetches works that have been allocated in Work Assignment from the backend.
   * Tracks their persistent savedGrade so that UI resets don't lose the database grade.
   */
  loadAssignedWorks(selectedWorkIdToRetain?: number): void {
    this.isLoading = true;
    this.workInspectionService.getAssignedWorks().subscribe({
      next: (works) => {
        this.assignedWorks = (works || []).map(w => ({
          ...w,
          savedGrade: w.currentGrade || 'Not Graded'
        }));
        if (selectedWorkIdToRetain) {
          const match = this.assignedWorks.find(w => w.workId === selectedWorkIdToRetain);
          if (match) {
            this.selectedWork = match;
          }
        }
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load assigned works from backend:', err);
        this.snackbar.showHttpError(err, 'Could not load the assigned works.');
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  /**
   * Invoked when user selects a work from the dropdown.
   * - Sets the Current Grade box to the work's saved grade from DB (or 'Not Graded' if never inspected).
   * - Loads empty grading dropdowns below so user can regrade from scratch.
   */
  onWorkChange(): void {
    this.isGradeCalculated = false;
    this.calculatedGradeResult = '';
    this.gradeItems = [];

    if (!this.selectedWork) {
      this.cdr.detectChanges();
      return;
    }

    // Ensure Current Grade box reflects the work's saved grade from the database
    this.selectedWork.currentGrade = this.selectedWork.savedGrade || this.selectedWork.currentGrade || 'Not Graded';

    const resolvedCategory = this.resolveWorkTypeCategory(this.selectedWork.workType);

    this.isLoading = true;
    this.cdr.detectChanges();
    this.workInspectionService.getGradeItems(resolvedCategory).subscribe({
      next: (items) => {
        this.gradeItems = (items || []).map(item => ({
          ...item,
          grade: '' as GradeValue
        }));
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load checklist items from backend:', err);
        this.snackbar.showHttpError(err, 'Could not load the checklist items.');
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  /**
   * Returns all 4 grade options for every item (no critical restrictions).
   * The authority has full discretion.
   */
  // One fixed list: the template asks for it on every check, and a new list each time made Angular
  // rebuild all the grade dropdowns again and again (NG0103 infinite change detection with many items)
  private static readonly GRADE_OPTIONS: { value: GradeValue; label: string }[] = [
    { value: '', label: '-- Select Grade --' },
    { value: 'S', label: 'S (Satisfactory)' },
    { value: 'SRI', label: 'SRI (Satisfactory Req. Improvement)' },
    { value: 'U', label: 'U (Unsatisfactory)' },
    { value: '--NA--', label: '--NA-- (Not Applicable)' }
  ];

  getAvailableGrades(): { value: GradeValue; label: string }[] {
    return WorkInspectionAndReportingComponent.GRADE_OPTIONS;
  }

  /**
   * Action: Calculate Overall Grade
   * - Evaluates all items.
   * - Immediately updates the Current Grade box on the screen and shows the green banner.
   */
  calculateGrade(): void {
    if (!this.selectedWork) {
      this.snackbar.showError('Please select an assigned work first.');
      return;
    }

    const unselectedCount = this.gradeItems.filter(i => !i.grade).length;
    if (unselectedCount > 0) {
      const confirmProceed = confirm(
        `There are still ${unselectedCount} item(s) un-graded. Would you like to calculate based on completed items?`
      );
      if (!confirmProceed) {
        return;
      }
    }

    const result = calculateOverallGrade(this.gradeItems);
    this.calculatedGradeResult = result;
    this.selectedWork.currentGrade = result;
    this.isGradeCalculated = true;
    this.cdr.detectChanges();
  }

  /**
   * Action: Clear / Reset Grades
   * - Resets all item grade dropdowns back to unselected.
   * - Restores the Current Grade box back to the work's saved grade (or 'Not Graded' if never inspected).
   * - Hides the calculated result banner.
   */
  clearGrades(): void {
    if (!this.selectedWork) {
      return;
    }
    if (confirm('Are you sure you want to clear and reset all entered grades?')) {
      this.gradeItems.forEach(item => (item.grade = ''));
      this.selectedWork.currentGrade = this.selectedWork.savedGrade || 'Not Graded';
      this.isGradeCalculated = false;
      this.calculatedGradeResult = '';
      this.cdr.detectChanges();
    }
  }

  /**
   * Action: Save Grades
   * - Ensures grade is calculated.
   * - Sends the inspection header and each individual item's grade to the database.
   * - Updates the work's savedGrade and syncs with backend so switching works retains the latest grade.
   */
  saveGrades(): void {
    if (!this.selectedWork) {
      this.snackbar.showError('Please select an assigned work first.');
      return;
    }

    if (!this.isGradeCalculated) {
      this.calculateGrade();
    }

    const payload: InspectionSavePayload = {
      workId: this.selectedWork.workId,
      workNumber: this.selectedWork.workNumber,
      overallGrade: this.selectedWork.currentGrade,
      remarks: '',
      items: this.gradeItems.map(item => ({
        itemId: item.itemId,
        itemNo: item.itemNo,
        awardedGrade: item.grade || '--NA--',
        remarks: item.remarks || ''
      }))
    };

    this.workInspectionService.saveInspection(payload).subscribe({
      next: (response) => {
        this.snackbar.showMessage(response);
        if (this.selectedWork) {
          this.selectedWork.savedGrade = this.selectedWork.currentGrade;
        }
        // Refresh assignedWorks in background to ensure dropdown and records stay fully in sync
        this.loadAssignedWorks(this.selectedWork?.workId);
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to save inspection report to backend:', err);
        this.snackbar.showHttpError(err, 'Failed to save the inspection report.');
        this.cdr.detectChanges();
      }
    });
  }

  /**
   * Action: Back button navigation
   */
  goBack(): void {
    this.router.navigate(['/home/qms/work-assignment/add']);
  }
}
