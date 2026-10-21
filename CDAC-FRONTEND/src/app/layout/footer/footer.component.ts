import { Component } from '@angular/core';

@Component({
  selector: 'app-footer',
  standalone: false,
  template: `
    <footer class="footer">
      <span>&copy; {{ year }} My Application</span>
    </footer>
  `,
  styleUrl: './footer.component.css',
})
export class FooterComponent {
  readonly year = new Date().getFullYear();
}
