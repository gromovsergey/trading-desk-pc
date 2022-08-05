import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {SharedModule} from '../shared/shared.module';
import {CreativeModule} from '../creative/creative.module';
import {ChartModule} from '../chart/chart.module';
import {FLIGHT_SERVICES} from './services';
import {FLIGHT_COMPONENTS, FLIGHT_EXPORTED_COMPONENTS} from './components';
import {AgencyService} from '../agency/services/agency.service';
import {AdvertiserService} from '../advertiser/services/advertiser.service';
import {ChannelService} from '../channel/services/channel.service';
import {LineItemService} from '../lineitem/services/lineitem.service';
import {ConversionService} from '../conversion/services/conversion.service';
import {FlightRoutingModule} from './flight-routing.module';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import {MatButtonToggleModule} from '@angular/material/button-toggle';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatAutocompleteModule} from '@angular/material/autocomplete';
import {MatTreeModule} from '@angular/material/tree';
import {MatTabsModule} from "@angular/material/tabs";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import { BidStrategyEditComponent } from './components/flight-lineitems/bid-strategy-edit/bid-strategy-edit.component';


@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        SharedModule,
        CreativeModule,
        ChartModule,
        FlightRoutingModule,
        MatProgressBarModule,
        MatButtonToggleModule,
        MatPaginatorModule,
        MatAutocompleteModule,
        MatTreeModule,
        MatTabsModule,
        ReactiveFormsModule,
        MatFormFieldModule,
        MatInputModule,
    ],
  providers: [
    ...FLIGHT_SERVICES,
    AgencyService,
    AdvertiserService,
    ChannelService,
    LineItemService,
    ConversionService
  ],
  declarations: [
    ...FLIGHT_COMPONENTS,
    BidStrategyEditComponent,
  ],
  exports: [
    ...FLIGHT_EXPORTED_COMPONENTS,
  ]
})
export class FlightModule {
}
