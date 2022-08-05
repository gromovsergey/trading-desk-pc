import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {SharedModule} from '../shared/shared.module';
import {FlightModule} from '../flight/flight.module';
import {ChartModule} from '../chart/chart.module';
import {LineItemRoutingModule} from './lineitem-routing.module';
import {LINEITEM_SERVICES} from './services';
import {LINEITEMS_COMPONENTS} from './components';

@NgModule({
  imports: [
    LineItemRoutingModule,
    CommonModule,
    SharedModule,
    FlightModule,
    ChartModule
  ],
  providers: [
    ...LINEITEM_SERVICES
  ],
  declarations: [
    ...LINEITEMS_COMPONENTS
  ]
})
export class LineItemModule {
}
