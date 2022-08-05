import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {AgentReportTotalComponent} from './components/agent-report-total/agent-report-total.component';
import {AgentReportMonthlyComponent} from './components/agent-report-monthly/agent-report-monthly.component';

const ROUTES: Routes = [
  {
    path: 'total',
    component: AgentReportTotalComponent
  },
  {
    path: 'monthly/:year/:month',
    component: AgentReportMonthlyComponent
  },
  {
    path: '**',
    redirectTo: 'total'
  }
];

@NgModule({
  imports: [
    RouterModule.forChild(ROUTES)
  ],
  exports: [RouterModule]
})
export class AgentReportRoutingModule {
}
