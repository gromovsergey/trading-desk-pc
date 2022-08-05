import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {SharedModule} from '../shared/shared.module';
import {ChannelRoutingModule} from './channel-routing.module';
import {ChannelListComponent} from './components/channel-list/channel-list.component';
import {CHANNEL_SERVICES} from './services';
import {CHANNEL_COMPONENTS} from './components';
import {AdvertiserService} from '../advertiser/services/advertiser.service';
import {AgencyService} from '../agency/services/agency.service';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    SharedModule,
    ChannelRoutingModule,
  ],
  providers: [
    ...CHANNEL_SERVICES,
    AdvertiserService,
    AgencyService,
  ],
  declarations: [
    ...CHANNEL_COMPONENTS
  ],
  exports: [
    ChannelListComponent
  ]
})
export class ChannelModule {
}

