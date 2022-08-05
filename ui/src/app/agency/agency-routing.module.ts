import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {AgencyComponent} from './components/agency/agency.component';
import {AgencyAdvertisersComponent} from './components/agency-advertisers/agency-advertisers.component';
import {AgencySearchComponent} from './components/agency-search/agency-search.component';
import {AgencyChannelsComponent} from './components/agency-channel/agency-channel.component';
import {AgencyDashboardComponent} from './components/agency-dashboard/agency-dashboard.component';
import {UserEditComponent} from '../user/containers/user-edit/user-edit.component';

const ROUTES: Routes = [
  {
    path: ':id/account',
    component: AgencyComponent,
    data: {
      title: 'accountSearch.account'
    }
  },
  {
    path: 'dashboard',
    component: AgencyDashboardComponent,
    data: {
      title: 'agencyAccount.dashboard'
    }
  },
  {
    path: ':id/dashboard',
    component: AgencyDashboardComponent,
    data: {
      title: 'agencyAccount.dashboard'
    }
  },
  {
    path: ':id/advertisers',
    component: AgencyAdvertisersComponent,
    data: {
      title: 'agencyAccount.advertisersSummary'
    }
  },
  {
    path: ':id/channels',
    component: AgencyChannelsComponent,
    data: {
      title: 'agencyAccount.channels'
    }
  },
  {
    path: 'select',
    component: AgencySearchComponent,
    data: {
      title: 'accountSearch.accounts'
    }
  },
  {
    path: ':id/user/add',
    component: UserEditComponent,
    data: {
      title: 'agencyAccount.user.add'
    }
  },
  {
    path: ':id/user/:userId/edit',
    component: UserEditComponent,
    data: {
      title: 'agencyAccount.user.edit'
    }
  }
];

@NgModule({
  imports: [
    RouterModule.forChild(ROUTES)
  ],
  exports: [RouterModule]
})

export class AgencyRoutingModule {
}
