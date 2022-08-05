import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

import {PublisherReportComponent} from './components/publisher-report/publisher-report.component';
import {ReferrerReportComponent} from './components/referrer-report/referrer-report.component';
import {DetailedReportComponent} from './components/detailed/detailed-report.component';

const ROUTES: Routes = [
    {path: 'publisher', component: PublisherReportComponent},
    {path: 'publisher/:id', component: PublisherReportComponent},
    {path: 'referrer', component: ReferrerReportComponent},
    {path: 'referrer/:id', component: ReferrerReportComponent},
    {path: 'detailed', component: DetailedReportComponent},
    {path: 'detailed/:id', component: DetailedReportComponent}
];

@NgModule({
    imports: [
        RouterModule.forChild(ROUTES)
    ],
    exports: [RouterModule]
})

export class ReportRoutingModule {}
