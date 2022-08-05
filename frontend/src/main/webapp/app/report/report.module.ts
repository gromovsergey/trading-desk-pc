import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';

import {SharedModule} from '../shared/shared.module';
import {ReportRoutingModule} from './report-routing.module';
import {PublisherReportService} from "./publisher_report.service";
import {PublisherReportComponent} from "./publisher_report.component";
import {ReportComponent} from "./report.component";
import {ReferrerReportService} from "./referrer_report.service";
import {ReferrerReportComponent} from "./referrer_report.component";
import {DetailedReportService} from "./detailed_report.service";
import {DetailedReportComponent} from "./detailed_report.component";

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        SharedModule,
        ReportRoutingModule
    ],
    providers: [
        PublisherReportService,
        ReferrerReportService,
        DetailedReportService
    ],
    declarations: [
        ReportComponent,
        PublisherReportComponent,
        ReferrerReportComponent,
        DetailedReportComponent
    ],
    exports: [
        ReportComponent
    ]
})

export class ReportModule {
}

