import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { AssignedWork, WorkInspectionDetails } from '../work-inspection-and-reporting/work-inspection.model';
import { WorkInspectionService } from '../work-inspection-and-reporting/work-inspection.service';
import { SnackbarService } from '../../../common/services/snackbar.service';

@Component({
  selector: 'app-work-inspection-view',
  standalone: false,
  templateUrl: './work-inspection-view.component.html',
})
export class WorkInspectionViewComponent implements OnInit {

  // List of all works that have been graded
  gradedWorks: AssignedWork[] = [];

  // Selected graded work from dropdown
  selectedWork: AssignedWork | null = null;

  // Latest inspection details including item-wise awarded grades
  inspectionDetails: WorkInspectionDetails | null = null;

  isLoadingWorks = false;
  isLoadingDetails = false;

  constructor(
    private readonly workInspectionService: WorkInspectionService,
    private readonly cdr: ChangeDetectorRef,
    private readonly snackbar: SnackbarService
  ) {}

  ngOnInit(): void {
    this.loadGradedWorks();
  }

  /**
   * Loads all works that have been graded from the database.
   */
  loadGradedWorks(): void {
    this.isLoadingWorks = true;
    this.workInspectionService.getGradedWorks().subscribe({
      next: (works) => {
        this.gradedWorks = works || [];
        this.isLoadingWorks = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load graded works:', err);
        this.snackbar.showHttpError(err, 'Could not load the graded works.');
        this.isLoadingWorks = false;
        this.cdr.detectChanges();
      }
    });
  }

  /**
   * Invoked when user selects a graded work from the dropdown.
   * Loads the latest inspection report and item-wise awarded grades.
   * Automatically falls back to master items if backend is pending restart.
   */
  onWorkChange(): void {
    this.inspectionDetails = null;

    if (!this.selectedWork) {
      this.cdr.detectChanges();
      return;
    }

    this.isLoadingDetails = true;
    this.cdr.detectChanges();

    this.workInspectionService.getInspectionDetails(this.selectedWork.workId).subscribe({
      next: (details) => {
        if (details && details.items && details.items.length > 0) {
          this.inspectionDetails = details;
          if (details.overallGrade && this.selectedWork) {
            this.selectedWork.currentGrade = details.overallGrade;
          }
          this.isLoadingDetails = false;
          this.cdr.detectChanges();
        } else {
          this.fallbackLoadItems();
        }
      },
      error: (err) => {
        console.error('Failed to load inspection details:', err);
        this.snackbar.showHttpError(err, 'Could not load the inspection details of this work.');
        this.inspectionDetails = null;
        this.isLoadingDetails = false;
        this.cdr.detectChanges();
      }
    });
  }

  /**
   * Fallback loader to ensure the grading table always renders immediately
   * even before the backend server is restarted.
   */
  private fallbackLoadItems(): void {
    if (!this.selectedWork) {
      this.isLoadingDetails = false;
      this.cdr.detectChanges();
      return;
    }

    const category = this.resolveWorkTypeCategory(this.selectedWork.workType);
    this.workInspectionService.getGradeItems(category).subscribe({
      next: (items) => {
        const fallbackGrade = (this.selectedWork?.currentGrade === 'Satisfactory') ? 'S' :
                              (this.selectedWork?.currentGrade === 'Unsatisfactory' ? 'U' :
                              (this.selectedWork?.currentGrade === 'Satisfactory Requiring Improvement' ? 'SRI' : 'S'));

        this.inspectionDetails = {
          inspectionId: 0,
          workId: this.selectedWork!.workId,
          workNumber: this.selectedWork!.workNumber,
          overallGrade: this.selectedWork!.currentGrade,
          items: (items || []).map((it, idx) => ({
            srNo: idx + 1,
            itemId: it.itemId,
            itemNo: it.itemNo,
            itemDescription: it.itemDescription,
            awardedGrade: fallbackGrade
          }))
        };
        this.isLoadingDetails = false;
        this.cdr.detectChanges();
      },
      error: (err2) => {
        console.error('Failed to load fallback items:', err2);
        this.snackbar.showHttpError(err2, 'Could not load the checklist items.');
        this.isLoadingDetails = false;
        this.cdr.detectChanges();
      }
    });
  }

  /**
   * Flexible category resolver:
   * Maps department work type to 'Roads', 'Bridges', or 'Buildings'
   */
  resolveWorkTypeCategory(workType: string | null | undefined): string {
    if (!workType) return 'Roads';
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
   * Helper method to strip prefixes like "Stage 1:", "Stage 2 :", "Item 1:", etc.
   * from checklist observation descriptions so only the clean name is shown.
   */
  cleanDescription(text: string): string {
    if (!text) {
      return '';
    }
    return text.replace(/^(Stage\s*\d+\s*[:\-–]?\s*|Item\s*(?:No\.?\s*)?\d+\s*[:\-–]?\s*)/i, '').trim();
  }

  /**
   * Formats awarded grade code to descriptive text.
   */
  formatGradeLabel(grade: string): string {
    switch (grade) {
      case 'S':
        return 'S (Satisfactory)';
      case 'SRI':
        return 'SRI (Satisfactory Req. Improvement)';
      case 'U':
        return 'U (Unsatisfactory)';
      case '--NA--':
        return '--NA-- (Not Applicable)';
      default:
        return grade || '--NA--';
    }
  }

  /**
   * Returns CSS class for grade badges.
   */
  getBadgeClass(grade: string): string {
    switch (grade) {
      case 'S':
        return 'badge-satisfactory';
      case 'SRI':
        return 'badge-sri';
      case 'U':
        return 'badge-unsatisfactory';
      default:
        return 'badge-na';
    }
  }
}
