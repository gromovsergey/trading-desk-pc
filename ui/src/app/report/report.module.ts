import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

import {SharedModule} from '../shared/shared.module';
import {ReportRoutingModule} from './report-routing.module';
import {ReportComponent} from './components/report/report.component';
import {PUBLISHER_COMPONENTS} from './components';
import {REPORT_SERVICES} from './services';
import {MatButtonToggleModule} from '@angular/material/button-toggle';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    SharedModule,
    ReportRoutingModule,
    ReactiveFormsModule,
    MatButtonToggleModule
  ],
  providers: [
    ...REPORT_SERVICES
  ],
  declarations: [
    ...PUBLISHER_COMPONENTS,
  ],
  exports: [
    ReportComponent
  ]
})
export class ReportModule {
}

