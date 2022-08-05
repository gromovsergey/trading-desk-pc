import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ChannelEditBehavioralComponent} from './components/channel-edit-behavioral/channel-edit-behavioral.component';
import {ChannelEditExpressionComponent} from './components/channel-edit-expression/channel-edit-expression.component';
import {ChannelSearchComponent} from './components/channel-search/channel-search.component';

const ROUTES: Routes = [
  {
    path: 'behavioral/add',
    component: ChannelEditBehavioralComponent
  },
  {
    path: 'behavioral/add/:id',
    component: ChannelEditBehavioralComponent
  },
  {
    path: 'behavioral/:id/edit',
    component: ChannelEditBehavioralComponent
  },
  {
    path: 'expression/add',
    component: ChannelEditExpressionComponent
  },
  {
    path: 'expression/add/:id',
    component: ChannelEditExpressionComponent
  },
  {
    path: 'expression/:id/edit',
    component: ChannelEditExpressionComponent
  },
  {
    path: 'search',
    component: ChannelSearchComponent
  },
];

@NgModule({
  imports: [
    RouterModule.forChild(ROUTES)
  ],
  exports: [RouterModule]
})
export class ChannelRoutingModule {
}
