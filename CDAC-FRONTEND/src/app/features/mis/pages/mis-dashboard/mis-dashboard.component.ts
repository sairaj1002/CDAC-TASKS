import { Component } from '@angular/core';

@Component({
  selector: 'app-mis',
  standalone: false,
  template: `
    <section class="page">
      <h1>{{ title }}</h1>
      <p>{{ title }} module works. Add your content here.</p>
    </section>
  `,
  styles: [`
    .page { padding: 8px 0; color: #12406b; }
    h1 { margin: 0 0 8px; font-size: 1.5rem; }
  `],
})
export class MisComponent {
  title = 'MIS';
}
