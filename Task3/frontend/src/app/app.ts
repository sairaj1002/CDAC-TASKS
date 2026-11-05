import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  standalone: false,
  templateUrl: './app.html',
})
export class App {
  sidebarVisible = true;
  activeSubMenu = 'pretender';

  toggleSidebar(): void {
    this.sidebarVisible = !this.sidebarVisible;
  }

  onSubMenuSelected(menuId: string): void {
    this.activeSubMenu = menuId;
  }
}
