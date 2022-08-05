import { NgModule }     from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule }  from '@angular/forms';

import { SharedModule }                   from '../shared/shared.module';
import { CreativeModule }                 from '../creative/creative.module';
import { UserModule }                     from '../user/user.module';
import { ConversionModule }               from '../conversion/conversion.module';
import { ChannelModule }                  from '../channel/channel.module';

import { AgencyService }                  from '../agency/agency.service';
import { FlightService }                  from '../flight/flight.service';
import { AdvertiserRoutingModule }        from './advertiser-routing.module';
import { AdvertiserService }              from './advertiser.service';
import { AdvertiserComponent }            from './advertiser.component';
import { AdvertiserFlightsComponent }     from './advertiser_flights.component';
import { AdvertiserCreativesComponent }   from './advertiser_creatives.component';
import { AdvertiserConversionsComponent } from './advertiser_conversions.component';
import { AdvertiserChannelsComponent }    from './advertiser_channels.component';
import { AdvertiserEditComponent }        from './advertiser_edit.component';
import { AdvertiserPropertiesComponent }  from './advertiser_properties.component';
import { AdvertiserReportComponent }      from './advertiser_report.component';
import { DomainsReportComponent }         from './domains_report.component';
import { ReportModule }                   from "../report/report.module";
import { AdvertiserReportService }        from "./advertiser_report.service";
import { DomainsReportService }           from "./domains_report.service";
import { ConversionsReportComponent }     from "./conversions_report.component";
import { SegmentsReportComponent }        from "./segments_report.component";
import { ConversionsReportService }       from "./conversions_report.service";
import { LineItemService }                from "../lineitem/lineitem.service";
import { ConversionService }              from "../conversion/conversion.service";
import {SegmentsReportService} from "./segments_report.service";

@NgModule({
  imports:      [
    AdvertiserRoutingModule,
    CommonModule,
    FormsModule,
    SharedModule,
    CreativeModule,
    UserModule,
    ConversionModule,
    ChannelModule,
    ReportModule
  ],
  providers:    [
    AdvertiserService,
    AgencyService,
    FlightService,
    LineItemService,
    ConversionService,
    AdvertiserReportService,
    DomainsReportService,
    ConversionsReportService,
    SegmentsReportService
  ],
  declarations: [
    AdvertiserComponent,
    AdvertiserFlightsComponent,
    AdvertiserCreativesComponent,
    AdvertiserConversionsComponent,
    AdvertiserChannelsComponent,
    AdvertiserEditComponent,
    AdvertiserPropertiesComponent,
    AdvertiserReportComponent,
    ConversionsReportComponent,
    DomainsReportComponent,
    SegmentsReportComponent
  ]
})

export class AdvertiserModule {}
