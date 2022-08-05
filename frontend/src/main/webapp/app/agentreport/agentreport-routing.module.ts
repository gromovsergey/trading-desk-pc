import { NgModule }                from '@angular/core';
import { RouterModule, Routes }    from '@angular/router';

import { AgentReportTotalComponent }   from './agentreport_total.component';
import { AgentReportMonthlyComponent } from './agentreport_monthly.component';

const ROUTES: Routes = [
  { path: 'total',                component: AgentReportTotalComponent },
  { path: 'monthly/:year/:month', component: AgentReportMonthlyComponent },
];

@NgModule({
  imports: [
    RouterModule.forChild(ROUTES)
  ],
  exports: [ RouterModule ]
})

export class AgentReportRoutingModule {}
