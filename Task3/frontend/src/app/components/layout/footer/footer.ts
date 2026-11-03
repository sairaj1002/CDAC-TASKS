import { Component } from '@angular/core';
import { APP_CONSTANTS } from '../../../common/constants/app.constants';

@Component({
  selector: 'app-wamis-footer',
  standalone: false,
  templateUrl: './footer.html',
})
export class WamisFooterComponent {
  org = APP_CONSTANTS.ORGANIZATION;
  dept = APP_CONSTANTS.DEPARTMENT;
  module = APP_CONSTANTS.MODULE_NAME;
}

