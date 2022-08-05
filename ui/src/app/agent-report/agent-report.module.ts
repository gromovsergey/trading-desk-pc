import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {SharedModule} from '../shared/shared.module';
import {AgentReportRoutingModule} from './agent-report-routing.module';
import {AGENT_REPORT_SERVICES} from './services';
import {AGENT_REPORT_COMPONENTS} from './components';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    SharedModule,
    AgentReportRoutingModule
  ],
  providers: [
    ...AGENT_REPORT_SERVICES
  ],
  declarations: [
    ...AGENT_REPORT_COMPONENTS
  ]
})
export class AgentReportModule {
}
