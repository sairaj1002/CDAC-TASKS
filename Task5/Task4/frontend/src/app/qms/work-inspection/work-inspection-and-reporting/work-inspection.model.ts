export type WorkType = 'Roads' | 'Bridges' | 'Buildings' | string;

export type GradeValue = 'S' | 'SRI' | 'U' | '--NA--' | '';

export interface AssignedWork {
  srNo?: number;
  workId: number;
  divisionOffice: string;
  workType: WorkType;
  workNumber: string;
  workDescription: string;
  dateAssigned: string;
  currentGrade: string;
  savedGrade?: string;
}

export interface GradeItem {
  itemId: number;
  itemNo: number;
  itemDescription: string;
  grade: GradeValue;
  remarks?: string;
}

export interface InspectionItemView {
  srNo?: number;
  itemId: number;
  itemNo: number;
  itemDescription: string;
  awardedGrade: string;
  remarks?: string;
}

export interface WorkInspectionDetails {
  inspectionId: number;
  workId: number;
  workNumber: string;
  overallGrade: string;
  remarks?: string;
  items: InspectionItemView[];
}

/**
 * Natural Overall Grading Calculation:
 * - If ANY item is rated 'U' (Unsatisfactory) -> Overall is 'Unsatisfactory'.
 * - Else if ANY item is rated 'SRI' -> Overall is 'Satisfactory Requiring Improvement'.
 * - Else if all graded items are 'S' (with '--NA--' ignored) -> Overall is 'Satisfactory'.
 * - If no items are graded yet -> 'Not Graded'.
 */
export function calculateOverallGrade(items: GradeItem[]): string {
  const gradedItems = items.filter(i => i.grade && i.grade !== '--NA--');
  if (gradedItems.length === 0) {
    return 'Not Graded';
  }

  // 1. Any 'U' makes overall Unsatisfactory
  const hasU = items.some(i => i.grade === 'U');
  if (hasU) {
    return 'Unsatisfactory';
  }

  // 2. Any 'SRI' makes overall Satisfactory Requiring Improvement
  const hasSRI = items.some(i => i.grade === 'SRI');
  if (hasSRI) {
    return 'Satisfactory Requiring Improvement';
  }

  // 3. All graded items are 'S'
  const allS = gradedItems.every(i => i.grade === 'S');
  if (allS) {
    return 'Satisfactory';
  }

  return 'Satisfactory';
}
