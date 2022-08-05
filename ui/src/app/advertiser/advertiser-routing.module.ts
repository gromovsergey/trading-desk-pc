import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {CreativeEditComponent} from '../creative/components/creative-edit/creative-edit.component';
import {AdvertiserFlightsComponent} from './components/advertiser-flights/advertiser-flights.component';
import {AdvertiserEditComponent} from './components/advertiser-edit/advertiser-edit.component';
import {AdvertiserReportComponent} from './components/advertiser-report/advertiser-report.component';
import {DomainsReportComponent} from './components/domains-report/domains-report.component';
import {ConversionsReportComponent} from './components/conversions-report/conversions-report.component';
import {SegmentsReportComponent} from './components/segments-report/segments-report.component';
import {AdvertiserComponent} from './components/advertiser/advertiser.component';
import {AdvertiserCreativesComponent} from './components/advertiser-creatives/advertiser-creatives.component';
import {AdvertiserConversionsComponent} from './components/advertiser-conversions/advertiser_conversions.component';
import {AdvertiserChannelsComponent} from './components/advertiser-channels/advertiser-channels.component';
import {UserEditComponent} from '../user/containers/user-edit/user-edit.component';


const ROUTES: Routes = [
  {
    path: 'add',
    component: AdvertiserEditComponent
  },
  {
    path: ':id/edit',
    component: AdvertiserEditComponent
  },
  {
    path: ':id/flights',
    component: AdvertiserFlightsComponent
  },
  {
    path: ':id/account',
    component: AdvertiserComponent,
    data: {
      title: 'advertiserAccount.advertiserAccount'
    }
  },
  {
    path: ':id/creatives',
    component: AdvertiserCreativesComponent,
    data: {
      title: 'advertiserAccount.accountCreatives',
      active: 'creatives',
    }
  },
  {
    path: ':id/conversions',
    component: AdvertiserConversionsComponent
  },
  // {
  //   path: ':id/channels',
  //   component: AdvertiserChannelsComponent,
  //   data: {
  //     title: 'advertiserAccount.advertiserChannels'
  //   }
  // },
  {
    path: ':id/report',
    component: AdvertiserReportComponent
  },
  {
    path: ':id/conversionsReport',
    component: ConversionsReportComponent
  },
  {
    path: ':id/segmentsReport',
    component: SegmentsReportComponent
  },
  {
    path: ':id/domainsReport',
    component: DomainsReportComponent
  },
  {
    path: ':id/creative/:creativeId/edit',
    component: CreativeEditComponent,
    data: {
      active: 'creatives',
    }
  },
  {
    path: ':id/creative/new/template/:templateId/size/:sizeId',
    component: CreativeEditComponent,
    data: {
      active: 'creatives',
    }
  },
  {
    path: ':id/user/add',
    component: UserEditComponent
  },
  {
    path: ':id/user/:userId/edit',
    component: UserEditComponent
  }
];

@NgModule({
  imports: [
    RouterModule.forChild(ROUTES)
  ],
  exports: [RouterModule]
})
export class AdvertiserRoutingModule {
}
