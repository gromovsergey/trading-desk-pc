import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {SharedModule} from '../shared/shared.module';
import {CreativeModule} from '../creative/creative.module';
import {UserModule} from '../user/user.module';
import {ConversionModule} from '../conversion/conversion.module';
import {ChannelModule} from '../channel/channel.module';
import {AgencyService} from '../agency/services/agency.service';
import {FlightService} from '../flight/services/flight.service';
import {AdvertiserRoutingModule} from './advertiser-routing.module';
import {ReportModule} from '../report/report.module';
import {LineItemService} from '../lineitem/services/lineitem.service';
import {ConversionService} from '../conversion/services/conversion.service';
import {ADVERTISER_SERVICES} from './services';
import {ADVERTISER_COMPONENTS} from './components';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import {MatDialogModule} from "@angular/material/dialog";
import {FlightModule} from "../flight/flight.module";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    SharedModule,
    AdvertiserRoutingModule,
    CreativeModule,
    UserModule,
    ConversionModule,
    ChannelModule,
    ReportModule,
    MatProgressBarModule,
    MatDialogModule,
    FlightModule
  ],
  providers: [
    ...ADVERTISER_SERVICES,
    AgencyService,
    FlightService,
    LineItemService,
    ConversionService
  ],
  declarations: [
    ...ADVERTISER_COMPONENTS,
  ]
})
export class AdvertiserModule {
}
