import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-empty-tab',
  standalone: false,
  templateUrl: './empty-tab.component.html'
})
export class EmptyTabComponent {
  @Input() tabName: string = '';
  @Input() message: string = 'To be built in future';
}

