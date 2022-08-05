import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {MySettingsComponent} from './containers/my-settings/my-settings.component';

const ROUTES: Routes = [
  {
    path: '',
    redirectTo: 'settings',
    pathMatch: 'full'
  },
  {
    path: 'settings',
    component: MySettingsComponent,
    data: {
      title: 'blockName.mySettings'
    }
  }
];

@NgModule({
  imports: [
    RouterModule.forChild(ROUTES)
  ],
  exports: [RouterModule]
})
export class UserRoutingModule {
}
