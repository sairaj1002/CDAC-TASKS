import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-json-viewer',
  standalone: false,
  templateUrl: './json-viewer.component.html'
})
export class JsonViewerComponent {
  @Input() title: string = 'JSON Payload';
  @Input() jsonContent: any = '';

  get formattedJson(): string {
    if (!this.jsonContent) return '';
    if (typeof this.jsonContent === 'string') {
      try {
        const parsed = JSON.parse(this.jsonContent);
        return JSON.stringify(parsed, null, 2);
      } catch (e) {
        return this.jsonContent;
      }
    }
    return JSON.stringify(this.jsonContent, null, 2);
  }

  copied: boolean = false;

  copyToClipboard(): void {
    if (navigator.clipboard && this.formattedJson) {
      navigator.clipboard.writeText(this.formattedJson).then(() => {
        this.copied = true;
        setTimeout(() => this.copied = false, 2000);
      });
    }
  }
}
