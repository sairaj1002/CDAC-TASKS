import { Component } from '@angular/core';

@Component({
  selector: 'app-road-and-bridge',
  standalone: false,
  templateUrl: './scope-master.html',
})
export class RoadAndBridgeComponent {
  selectedEntityIndex = 0;
}

export { RoadAndBridgeComponent as ScopeMaster };
