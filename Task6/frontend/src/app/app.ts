import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { LoadingService } from './common/services/loading.service';
import { UserContextService } from './common/services/user-context.service';

@Component({
  selector: 'app-root',
  standalone: false,
  // Same as the QMS AppComponent; with the default (on-push) nothing below the root refreshes after HTTP calls
  changeDetection: ChangeDetectionStrategy.Eager,
  templateUrl: './app.html',
})
export class App {
  // Reads ?userId (QMS) from the address before the first navigation changes it
  private readonly userContext = inject(UserContextService);

  constructor(public loadingService: LoadingService) {
    // the portal shows its loader while the home page starts up
    this.loadingService.show();
    setTimeout(() => this.loadingService.hide(), 700);
  }
}
