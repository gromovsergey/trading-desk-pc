import { NgModule }                from '@angular/core';
import { RouterModule, Routes }    from '@angular/router';

import { AgencyComponent }            from './agency.component';
import { AgencyAdvertisersComponent } from './agency_advertisers.component';
import { AgencySearchComponent }      from './agency_search.component';
import { AgencyChannelsComponent }    from './agency_channel.component';
import { AgencyDashboardComponent }   from './agency_dashboard.component';
import { UserEditComponent }          from '../user/user_edit.component';

const ROUTES: Routes = [
  { path: ':id/account',                                      component: AgencyComponent },
  { path: 'dashboard',                                        component: AgencyDashboardComponent },
  { path: ':id/dashboard',                                    component: AgencyDashboardComponent },
  { path: ':id/advertisers',                                  component: AgencyAdvertisersComponent },
  { path: ':id/channels',                                     component: AgencyChannelsComponent },
  { path: 'select',                                           component: AgencySearchComponent },

  { path: ':id/user/add',                                     component: UserEditComponent},
  { path: ':id/user/:userId/edit',                            component: UserEditComponent}
];

@NgModule({
  imports: [
    RouterModule.forChild(ROUTES)
  ],
  exports: [ RouterModule ]
})

export class AgencyRoutingModule {}
