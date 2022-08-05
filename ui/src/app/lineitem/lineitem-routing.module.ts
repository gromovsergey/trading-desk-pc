import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LineItemComponent} from './components/lineitem/lineitem.component';
import {LineItemEditComponent} from './components/lineitem-edit/lineitem-edit.component';

const ROUTES: Routes = [
  {path: 'add', component: LineItemEditComponent},
  {path: ':id/edit', component: LineItemEditComponent},
  {path: ':id', component: LineItemComponent}
];

@NgModule({
  imports: [
    RouterModule.forChild(ROUTES)
  ],
  exports: [RouterModule]
})
export class LineItemRoutingModule {
}
