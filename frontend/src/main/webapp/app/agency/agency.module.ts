import { NgModule }     from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule }  from '@angular/forms';

import { SharedModule }               from '../shared/shared.module';
import { UserModule }                 from '../user/user.module';
import { ChannelModule }              from '../channel/channel.module';
import { AdvertiserService }          from '../advertiser/advertiser.service';
import { AgencyRoutingModule }        from './agency-routing.module';
import { AgencyService }              from './agency.service';
import { DashboardService }           from './dashboard.service';
import { AgencyComponent }            from './agency.component';
import { AgencyAdvertisersComponent } from './agency_advertisers.component';
import { AgencySearchComponent }      from './agency_search.component';
import { AgencyChannelsComponent }    from './agency_channel.component';
import { AgencyDashboardComponent }   from './agency_dashboard.component';
import { AgencyPropertiesComponent }  from './agency_properties.component';

@NgModule({
  imports:      [
    CommonModule,
    SharedModule,
    FormsModule,
    UserModule,
    ChannelModule,
    AgencyRoutingModule
  ],
  providers:    [
    AdvertiserService,
    AgencyService,
    DashboardService
  ],
  declarations: [
    AgencyComponent,
    AgencyAdvertisersComponent,
    AgencySearchComponent,
    AgencyChannelsComponent,
    AgencyDashboardComponent,
    AgencyPropertiesComponent
  ]
})

export class AgencyModule {}

