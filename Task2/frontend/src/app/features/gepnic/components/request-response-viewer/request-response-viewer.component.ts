import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-request-response-viewer',
  standalone: false,
  templateUrl: './request-response-viewer.component.html'
})
export class RequestResponseViewerComponent {
  @Input() requestPayload: any = null;
  @Input() responsePayload: any = null;
  @Input() status: string = 'SUCCESS';
  @Input() responseCode: number = 1;
}
