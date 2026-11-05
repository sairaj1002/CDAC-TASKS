import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-wamis-master',
  standalone: false,
  templateUrl: './wamis-master.html',
})
export class WamisMasterComponent {
  @Input() activeSubMenu = 'pretender';
}

