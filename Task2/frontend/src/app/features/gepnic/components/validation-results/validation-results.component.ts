import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-validation-results',
  standalone: false,
  templateUrl: './validation-results.component.html'
})
export class ValidationResultsComponent {
  @Input() isValid: boolean = true;
  @Input() validationErrors: string[] = [];
  @Input() checkedRulesCount: number = 14;
}
