import {Component, Input} from '@angular/core';

@Component({
  selector: 'ui-flight-tabs',
  templateUrl: './flight-tabs.component.html',
  styleUrls: ['./flight-tabs.component.scss']
})
export class FlightTabsComponent {

  @Input() lineItems: any[];
  @Input() flightId: number;
  @Input() lineItemId: number;
  @Input() canCreate: boolean;
  @Input() userRole: string;
}
