import { NgModule }                from '@angular/core';
import { RouterModule, Routes }    from '@angular/router';

import { CreativeEditComponent }          from '../creative/creative_edit.component';
import { UserEditComponent }              from '../user/user_edit.component';
import { AdvertiserComponent }            from './advertiser.component';
import { AdvertiserFlightsComponent }     from './advertiser_flights.component';
import { AdvertiserCreativesComponent }   from './advertiser_creatives.component';
import { AdvertiserConversionsComponent } from './advertiser_conversions.component';
import { AdvertiserChannelsComponent }    from './advertiser_channels.component';
import { AdvertiserEditComponent }        from './advertiser_edit.component';
import { AdvertiserReportComponent }      from './advertiser_report.component';
import { DomainsReportComponent }         from "./domains_report.component";
import { ConversionsReportComponent }     from "./conversions_report.component";
import { SegmentsReportComponent }        from "./segments_report.component";

const ROUTES: Routes = [
  { path: 'add',  component: AdvertiserEditComponent },
  { path: ':id/edit',  component: AdvertiserEditComponent },
  { path: ':id/flights',  component: AdvertiserFlightsComponent },
  { path: ':id/account',  component: AdvertiserComponent },
  { path: ':id/creatives',  component: AdvertiserCreativesComponent },
  { path: ':id/conversions',  component: AdvertiserConversionsComponent },
  { path: ':id/channels',  component: AdvertiserChannelsComponent },
  { path: ':id/report',  component: AdvertiserReportComponent },
  { path: ':id/conversionsReport',  component: ConversionsReportComponent },
  { path: ':id/domainsReport',  component: DomainsReportComponent },
  { path: ':id/segmentsReport',  component: SegmentsReportComponent },

  { path: ':id/creative/:creativeId/edit',  component: CreativeEditComponent },
  { path: ':id/creative/new/template/:templateId/size/:sizeId',  component: CreativeEditComponent },
  { path: ':id/user/add',  component: UserEditComponent},
  { path: ':id/user/:userId/edit',   component: UserEditComponent}
];

@NgModule({
  imports: [
    RouterModule.forChild(ROUTES)
  ],
  exports: [ RouterModule ]
})

export class AdvertiserRoutingModule {}
