import { Component } from '@angular/core';

@Component({
  selector: 'app-wms-work-list',
  standalone: false,
  templateUrl: './work-list.component.html',

  styles: [
    `
      .page {
        padding: 8px 0;
        color: #12406b;
      }
      h1 {
        margin: 0 0 8px;
        font-size: 1.5rem;
      }
    `,
  ],
})
export class WmsWorkListComponent {
  title = 'WMS';
}
