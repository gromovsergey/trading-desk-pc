import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';

import {SharedModule} from '../shared/shared.module';
import {AudienceResearchRoutingModule} from "./audienceresearch-routing.module";
import {AudienceResearchService} from "./audienceresearch.service";
import {AudienceResearchListComponent} from "./audienceresearch-list.component";
import {AudienceResearchEditComponent} from "./audienceresearch-edit.component";
import {AudienceResearchChartComponent} from "./audienceresearch-chart.component";
import {AudienceResearchViewComponent} from "./audienceresearch-view.component";

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        SharedModule,
        AudienceResearchRoutingModule
    ],
    providers: [
        AudienceResearchService
    ],
    declarations: [
        AudienceResearchListComponent,
        AudienceResearchEditComponent,
        AudienceResearchChartComponent,
        AudienceResearchViewComponent
    ]
})

export class AudienceResearchModule {
}
