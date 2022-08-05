import { NgModule }                from '@angular/core';
import { RouterModule, Routes }    from '@angular/router';

import { LineItemComponent }     from './lineitem.component';
import { LineItemEditComponent } from './lineitem-edit.component';

const ROUTES: Routes = [
  { path: 'add',      component: LineItemEditComponent },
  { path: ':id/edit', component: LineItemEditComponent },
  { path: ':id',      component: LineItemComponent }
];

@NgModule({
  imports: [
    RouterModule.forChild(ROUTES)
  ],
  exports: [ RouterModule ]
})

export class LineItemRoutingModule {}