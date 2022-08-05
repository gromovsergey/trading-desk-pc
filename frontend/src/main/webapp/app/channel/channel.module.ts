import { NgModule }     from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule }  from '@angular/forms';

import { SharedModule }                   from '../shared/shared.module';
import { ChannelRoutingModule }           from './channel-routing.module';
import { ChannelService }                 from './channel.service';
import { ChannelListComponent }               from './channel_list.component';
import { ChannelSearchComponent }         from './channel_search.component';
import { ChannelEditBehavioralComponent } from './channel_edit_behavioral.component';
import { TriggersEditComponent }          from './trigger_edit.component';
import { ChannelEditExpressionComponent } from "./channel_edit_expression.component";
import {AdvertiserService} from "../advertiser/advertiser.service";
import {AgencyService} from "../agency/agency.service";

@NgModule({
  imports:      [
    CommonModule,
    FormsModule,
    SharedModule,
    ChannelRoutingModule
  ],
  providers:    [
    ChannelService,
    AdvertiserService,
    AgencyService
  ],
  declarations: [
    ChannelListComponent,
    ChannelSearchComponent,
    ChannelEditBehavioralComponent,
    ChannelEditExpressionComponent,
    TriggersEditComponent
  ],
  exports: [
      ChannelListComponent
  ]
})

export class ChannelModule {}

