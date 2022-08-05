import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {SharedModule} from '../shared/shared.module';
import {UserRoutingModule} from './user-routing.module';
import {UserService} from './services/user.service';
import {USER_CONTAINERS} from './containers';
import {UserListComponent} from './containers/user-list/user-list.component';
import {StatusVersionComponent} from "./containers/status-version/status-version.component";

@NgModule({
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    SharedModule,
    UserRoutingModule
  ],
  providers: [
    UserService
  ],
  declarations: [
    ...USER_CONTAINERS,
    StatusVersionComponent
  ],
  exports: [
    UserListComponent
  ]
})
export class UserModule {
}

