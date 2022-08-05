import { NgModule }     from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule }  from '@angular/forms';

import { SharedModule }        from '../shared/shared.module';
import { UserRoutingModule }   from './user-routing.module';
import { UserService }         from './user.service';
import { MySettingsComponent } from './my_settings.component';
import { UserEditComponent }   from './user_edit.component';
import { UserListComponent }   from './user_list.component';

@NgModule({
  imports:      [
    CommonModule,
    RouterModule,
    FormsModule,
    SharedModule,
    UserRoutingModule
  ],
  providers:    [ UserService ],
  declarations: [
    MySettingsComponent,
    UserEditComponent,
    UserListComponent
  ],
  exports: [
    UserEditComponent,
    UserListComponent
  ]
})

export class UserModule {}

