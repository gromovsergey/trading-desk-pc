import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

import {AudienceResearchEditComponent} from "./audienceresearch-edit.component";
import {AudienceResearchListComponent} from "./audienceresearch-list.component";
import {AudienceResearchViewComponent} from "./audienceresearch-view.component";

const ROUTES: Routes = [
    {path: 'list', component: AudienceResearchListComponent},
    {path: 'add', component: AudienceResearchEditComponent},
    {path: ':id/edit', component: AudienceResearchEditComponent},
    {path: ':id/view', component: AudienceResearchViewComponent}
];

@NgModule({
    imports: [
        RouterModule.forChild(ROUTES)
    ],
    exports: [RouterModule]
})

export class AudienceResearchRoutingModule {
}
