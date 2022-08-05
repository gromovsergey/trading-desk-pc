import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {LoginComponent, LogoutComponent} from './containers';
import {RouterModule, Routes} from '@angular/router';
import {SharedModule} from '../shared/shared.module';
import {ReactiveFormsModule} from '@angular/forms';
import {LoginService} from './services/login.service';
import {LogoModule} from "../logo/logo.module";

const routes: Routes = [
  {
    path: '',
    component: LoginComponent,
    data: {
      title: 'login.blockName.login'
    }
  },
  {
    path: 'logout',
    component: LogoutComponent,
    data: {
      title: 'blockName.logout'
    }
  },
];

@NgModule({
  declarations: [
    LoginComponent,
    LogoutComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    ReactiveFormsModule,
    LogoModule,
    RouterModule.forChild(routes)
  ],
  providers: [
    LoginService
  ]
})
export class LoginModule {
}
