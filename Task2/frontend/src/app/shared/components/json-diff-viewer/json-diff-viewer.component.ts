import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-json-diff-viewer',
  standalone: false,
  templateUrl: './json-diff-viewer.component.html'
})
export class JsonDiffViewerComponent {
  @Input() leftTitle: string = 'Source (WAMIS)';
  @Input() leftJson: any = '';
  @Input() rightTitle: string = 'Target (GePNIC Mapped)';
  @Input() rightJson: any = '';

  formatJson(data: any): string {
    if (!data) return '';
    if (typeof data === 'string') {
      try {
        return JSON.stringify(JSON.parse(data), null, 2);
      } catch (e) {
        return data;
      }
    }
    return JSON.stringify(data, null, 2);
  }
}
