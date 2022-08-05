import { NgModule }                from '@angular/core';
import { RouterModule, Routes }    from '@angular/router';

import { AuthGuard }               from './common/auth.guard';
import { DefaultPathComponent }    from "./common/default_path.component";
import { DashboardComponent }      from './common/dashboard.component';
import { TestComponent }           from './common/test.component';
import { LayoutComponent }         from './common/layout.component';
import { LayoutCleanComponent }    from './common/layout_clean.component';
import { LayoutEmptyComponent }    from './common/layout_empty.component';
import { LoginComponent }          from './common/login.component';
import { ErrorComponent }          from './common/error.component';

export const ROUTES: Routes = [
  {
      path: '',
      component: DefaultPathComponent,
      pathMatch: 'full'
  },
  {
      path: 'advertiser',
      component: LayoutComponent,
      loadChildren: './advertiser/advertiser.module#AdvertiserModule',
      canLoad: [ AuthGuard ],
      canActivateChild: [ AuthGuard ]
  },
  {
      path: 'agency',
      component: LayoutComponent,
      loadChildren: './agency/agency.module#AgencyModule',
      canLoad: [ AuthGuard ],
      canActivateChild: [ AuthGuard ]
  },
  {
      path: 'channel',
      component: LayoutComponent,
      loadChildren: './channel/channel.module#ChannelModule',
      canLoad: [ AuthGuard ],
      canActivateChild: [ AuthGuard ]
  },
  {
      path: 'conversion',
      component: LayoutComponent,
      loadChildren: './conversion/conversion.module#ConversionModule',
      canLoad: [ AuthGuard ],
      canActivateChild: [ AuthGuard ]
  },
  {
      path: 'flight',
      component: LayoutComponent,
      loadChildren: './flight/flight.module#FlightModule',
      canLoad: [ AuthGuard ],
      canActivateChild: [ AuthGuard ]
  },
  {
      path: 'lineitem',
      component: LayoutComponent,
      loadChildren: './lineitem/lineitem.module#LineItemModule',
      canLoad: [ AuthGuard ],
      canActivateChild: [ AuthGuard ]
  },
  {
      path: 'my',
      component: LayoutComponent,
      loadChildren: './user/user.module#UserModule',
      canLoad: [ AuthGuard ],
      canActivateChild: [ AuthGuard ]
  },
  {
      path: 'agentreport',
      component: LayoutComponent,
      loadChildren: './agentreport/agentreport.module#AgentReportModule',
      canLoad: [ AuthGuard ],
      canActivateChild: [ AuthGuard ]
  },
  {
      path: 'audienceresearch',
      component: LayoutComponent,
      loadChildren: './audienceresearch/audienceresearch.module#AudienceResearchModule',
      canLoad: [ AuthGuard ],
      canActivateChild: [ AuthGuard ]
  },
  {
      path: 'report',
      component: LayoutComponent,
      loadChildren: './report/report.module#ReportModule',
      canLoad: [ AuthGuard ],
      canActivateChild: [ AuthGuard ]
  },
  {
      path: 'cinemagoers',
      component: LayoutComponent,
      loadChildren: './cinemagoers/cinemagoers.module#CinemagoersModule',
      canLoad: [ AuthGuard ],
      canActivateChild: [ AuthGuard ]
  },
  {
      path: 'dashboard',
      component: LayoutComponent,
      children: [
          { path: '',  component: DashboardComponent },
          { path: 'test',  component: TestComponent },
      ],
      canActivateChild: [ AuthGuard ]
  },
  {
      path: '',
      component: LayoutEmptyComponent,
      children: [
          { path: 'login',  component: LoginComponent },
          { path: 'logout',  component: LoginComponent },
      ]
  },
  {
      path: 'error',
      component: LayoutEmptyComponent,
      children: [
          { path: '',  component: ErrorComponent },
          { path: '403',  component: ErrorComponent },
          { path: '404',  component: ErrorComponent },
          { path: '500',  component: ErrorComponent },
      ]
  },
  {
      path: '**',
      redirectTo: '/error/404'
  }
];

@NgModule({
  imports: [
    RouterModule.forRoot(ROUTES)
  ],
  exports: [ RouterModule ]
})

export class AppRoutingModule {}
