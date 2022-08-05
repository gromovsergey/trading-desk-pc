import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {DefaultPathComponent} from './common/default_path.component';
import {LayoutComponent} from './shared/containers/layout/layout.component';
import {DashboardComponent} from './common/dashboard.component';
import {AuthGuard} from './common/services/auth.guard';
import {ReloadComponent} from './shared/containers/reload/reload.component';
import {StatusVersionComponent} from "./user/containers/status-version/status-version.component";


const routes: Routes = [
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },
  {
    path: 'defaultPath',
    component: DefaultPathComponent,
    pathMatch: 'full',
    data: {
      skipUrl: true
    }
  },
  {
    path: 'advertiser',
    component: LayoutComponent,
    loadChildren: () => import('./advertiser/advertiser.module').then(m => m.AdvertiserModule),
    canLoad: [AuthGuard],
    canActivateChild: [AuthGuard]
  },
  {
    path: 'agency',
    component: LayoutComponent,
    loadChildren: () => import('./agency/agency.module').then(m => m.AgencyModule),
    canLoad: [AuthGuard],
    canActivateChild: [AuthGuard]
  },
  {
    path: 'channel',
    component: LayoutComponent,
    loadChildren: () => import('./channel/channel.module').then(m => m.ChannelModule),
    canLoad: [AuthGuard],
    canActivateChild: [AuthGuard]
  },
  {
    path: 'conversion',
    component: LayoutComponent,
    loadChildren: () => import('./conversion/conversion.module').then(m => m.ConversionModule),
    canLoad: [AuthGuard],
    canActivateChild: [AuthGuard]
  },
  {
    path: 'flight',
    component: LayoutComponent,
    loadChildren: () => import('./flight/flight.module').then(m => m.FlightModule),
    canLoad: [AuthGuard],
    canActivateChild: [AuthGuard]
  },
  {
    path: 'lineitem',
    component: LayoutComponent,
    loadChildren: () => import('./lineitem/lineitem.module').then(m => m.LineItemModule),
    canLoad: [AuthGuard],
    canActivateChild: [AuthGuard]
  },
  {
    path: 'my',
    component: LayoutComponent,
    loadChildren: () => import('./user/user.module').then(m => m.UserModule),
    canLoad: [AuthGuard],
    canActivateChild: [AuthGuard]
  },
  {
    path: 'agent-report',
    component: LayoutComponent,
    loadChildren: () => import('./agent-report/agent-report.module').then(m => m.AgentReportModule),
    canLoad: [AuthGuard],
    canActivateChild: [AuthGuard]
  },
  {
    path: 'report',
    component: LayoutComponent,
    loadChildren: () => import('./report/report.module').then(m => m.ReportModule),
    canLoad: [AuthGuard],
    canActivateChild: [AuthGuard]
  },
  {
    path: 'dashboard',
    component: LayoutComponent,
    children: [
      {path: '', component: DashboardComponent},
    ],
    canActivateChild: [AuthGuard]
  },
  {
    path: 'login',
    loadChildren: () => import('./login/login.module').then(m => m.LoginModule),
    data: {
      skipUrl: true
    },
  },
  {
    path: 'error',
    loadChildren: () => import('./error/error.module').then(m => m.ErrorModule),
    data: {
      skipUrl: true
    },
  },
  {
    path: 'reload',
    component: ReloadComponent,
    data: {
      skipUrl: true
    },
  },
  {
    path: 'status/version',
    component: StatusVersionComponent,
  },
  {
    path: '**',
    redirectTo: '/error/404'
  }
];


@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
