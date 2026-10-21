import { Component, signal } from '@angular/core';

@Component({
  selector: 'app-main-layout',
  standalone: false,
  templateUrl: './main-layout.component.html',
  styleUrl: './main-layout.component.css',
})
export class MainLayoutComponent {
  expanded = signal(false);


  toggleDrawer() {
    this.expanded.update((value) => !value);
  }
}
