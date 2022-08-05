import { NgModule }     from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule }  from '@angular/forms';

import { SharedModule }                from '../shared/shared.module';
import { TableFilterComponent }        from '../common/table_filter.component';
import { AgentReportRoutingModule }    from './agentreport-routing.module';
import { AgentReportService }          from './agentreport.service';
import { AgentReportMonthlyComponent } from './agentreport_monthly.component';
import { AgentReportTotalComponent }   from './agentreport_total.component';

@NgModule({
  imports:      [
    CommonModule,
    FormsModule,
    SharedModule,
    AgentReportRoutingModule
  ],
  providers:    [ AgentReportService ],
  declarations: [
    AgentReportMonthlyComponent,
    AgentReportTotalComponent,
    TableFilterComponent
  ]
})

export class AgentReportModule {}
