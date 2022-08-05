import { NgModule }     from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule }               from '../shared/shared.module';
import { FlightModule }               from '../flight/flight.module';
import { ChartModule }                from '../chart/chart.module';
import { LineItemRoutingModule }      from './lineitem-routing.module';
import { LineItemService }            from './lineitem.service';
import { LineItemComponent }          from './lineitem.component';
import { LineItemEditComponent }      from './lineitem-edit.component';

@NgModule({
  imports:      [
    LineItemRoutingModule,
    CommonModule,
    SharedModule,
    FlightModule,
    ChartModule
  ],
  providers:    [
    LineItemService
  ],
  declarations: [
    LineItemComponent,
    LineItemEditComponent
  ]
})

export class LineItemModule {}
