import { NgModule }                from '@angular/core';
import { RouterModule, Routes }    from '@angular/router';

import { MySettingsComponent }     from './my_settings.component';

const ROUTES: Routes = [
  { path: 'settings', component: MySettingsComponent }
];

@NgModule({
  imports: [
    RouterModule.forChild(ROUTES)
  ],
  exports: [ RouterModule ]
})

export class UserRoutingModule {}
