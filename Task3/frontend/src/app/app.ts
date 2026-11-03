import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  standalone: false,
  templateUrl: './app.html',
})
export class App {
  sidebarVisible = true;
  activeTab = 0;

  toggleSidebar(): void {
    this.sidebarVisible = !this.sidebarVisible;
  }

  onTabSelected(tabIndex: any): void {
    this.activeTab = typeof tabIndex === 'number' ? tabIndex : (tabIndex?.detail ?? 0);
  }
}
