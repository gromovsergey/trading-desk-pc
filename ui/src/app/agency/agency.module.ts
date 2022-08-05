import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {SharedModule} from '../shared/shared.module';
import {UserModule} from '../user/user.module';
import {ChannelModule} from '../channel/channel.module';
import {AgencyRoutingModule} from './agency-routing.module';
import {AdvertiserModule} from '../advertiser/advertiser.module';
import {AGENCY_COMPONENTS} from './components';
import {AGENCY_SERVICES} from './services';
import {MatProgressBarModule} from '@angular/material/progress-bar';


@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    FormsModule,
    UserModule,
    ChannelModule,
    AgencyRoutingModule,
    AdvertiserModule,
    ReactiveFormsModule,
    MatProgressBarModule,
  ],
  providers: [
    ...AGENCY_SERVICES,
  ],
  declarations: [
    ...AGENCY_COMPONENTS,
  ]
})
export class AgencyModule {
}

