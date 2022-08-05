import { NgModule }                from '@angular/core';
import { RouterModule, Routes }    from '@angular/router';

import { ConversionEditComponent } from './conversion_edit.component';

const ROUTES: Routes = [
  { path: 'add',      component: ConversionEditComponent },
  { path: ':id/edit', component: ConversionEditComponent },
];

@NgModule({
  imports: [
    RouterModule.forChild(ROUTES)
  ],
  exports: [ RouterModule ]
})

export class ConversionRoutingModule {}