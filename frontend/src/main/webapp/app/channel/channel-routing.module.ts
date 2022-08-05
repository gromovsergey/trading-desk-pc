import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

import {ChannelEditBehavioralComponent} from './channel_edit_behavioral.component';
import {ChannelEditExpressionComponent} from "./channel_edit_expression.component";
import {ChannelSearchComponent} from "./channel_search.component";

const ROUTES: Routes = [
    {path: 'behavioral/add', component: ChannelEditBehavioralComponent},
    {path: 'behavioral/add/:id', component: ChannelEditBehavioralComponent},
    {path: 'behavioral/:id/edit', component: ChannelEditBehavioralComponent},
    {path: 'expression/add', component: ChannelEditExpressionComponent},
    {path: 'expression/add/:id', component: ChannelEditExpressionComponent},
    {path: 'expression/:id/edit', component: ChannelEditExpressionComponent},
    {path: 'search', component: ChannelSearchComponent},
];

@NgModule({
    imports: [
        RouterModule.forChild(ROUTES)
    ],
    exports: [RouterModule]
})

export class ChannelRoutingModule {
}