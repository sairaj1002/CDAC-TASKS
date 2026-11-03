import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';
import { AccessService } from '../common/services/access.service';
import { RbacModule, RbacSubmodule } from '../common/forms/rbac-menu';
import { MENU_MODULES, MenuFunction, MenuModule, MenuSubmodule } from './menu.config';

@Component({
  selector: 'app-home',
  standalone: false,
  // Same as the QMS HomeComponent; with the default (on-push) the pages inside the outlet do not refresh after HTTP calls
  changeDetection: ChangeDetectionStrategy.Eager,
  templateUrl: './home.component.html',
})
export class HomeComponent {
  // WMS and the empty placeholder modules come from menu.config. QMS is listed too, but its menu is what the QMS
  // backend sent for this user (only the screens the user may use); until then, or without any right, it has none.
  modules: MenuModule[] = MENU_MODULES.map(module => module.moduleCode === HomeComponent.QMS ? { ...module, submodules: [] } : module);
  private static readonly QMS = 'qms';
  selectedModule?: MenuModule;
  isRightSectionVisible = true;
  isSidebarVisible = true;

  officeName = 'TEST DIVISION OFFICE';
  profile = 'VKJ';
  version = '1.0.35+26.10.13 (Updated on 16.10.2026)';

  constructor(private readonly router: Router, readonly access: AccessService) {
    // Loads what the user may do in QMS; the message for an unknown id or an unreachable server is shown when QMS is opened
    this.access.load().subscribe(() => {
      this.modules = this.modules.map(module => module.moduleCode === HomeComponent.QMS ? this.qmsFromBackend(module) : module);
    });
  }

  private qmsFromBackend(placeholder: MenuModule): MenuModule {
    const tree: RbacModule | undefined = this.access.modules().find(module => module.moduleCode === HomeComponent.QMS);
    const submodules = tree ? HomeComponent.flatten(tree.submodules) : [];
    return { ...placeholder, submodules };
  }

  // A submodule inside a submodule becomes a submodule of its own; the menu has one level of submodules
  private static flatten(submodules: RbacSubmodule[]): MenuSubmodule[] {
    return submodules.flatMap(submodule => [
      ...(submodule.functions.length
        ? [{
          name: submodule.name,
          functions: submodule.functions.map(fn => ({ name: fn.name, path: fn.path, functionCode: fn.functionCode })),
        }]
        : []),
      ...HomeComponent.flatten(submodule.submodules ?? []),
    ]);
  }

  toggleSubMenu(module: MenuModule): void {
    // Modules without a submenu have no flyout; QMS without a menu says why (unknown user, no rights, server down)
    if (!module.submodules.length) {
      this.selectedModule = undefined;
      if (module.moduleCode === HomeComponent.QMS) {
        this.access.notifyNoAccess();
      }
      return;
    }
    // The menu was minimized after a function was chosen: clicking its module again opens it
    if (this.selectedModule === module && !this.isRightSectionVisible) {
      this.isRightSectionVisible = true;
      return;
    }
    this.isRightSectionVisible = true;
    this.selectedModule = this.selectedModule === module ? undefined : module;
  }

  // Same navigation as the reference menu: /home/<function path>
  showFunction(fn: MenuFunction): void {
    if (fn.disabled) {
      return;
    }
    const pathSegments = this.access.pathFor(fn).split('/').filter(segment => segment !== '');
    this.router.navigate(['home', ...pathSegments]);
    // Minimize the menu so only the chosen page is visible (the reference menu does the same)
    this.isRightSectionVisible = false;
  }

  goHome(): void {
    this.selectedModule = undefined;
    this.router.navigate(['home']);
  }
}
