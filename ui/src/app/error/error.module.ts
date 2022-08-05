import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule, Routes} from '@angular/router';
import {SharedModule} from '../shared/shared.module';
import {ReactiveFormsModule} from '@angular/forms';
import {ErrorComponent} from './containers/error/error.component';

const routes: Routes = [
  {
    path: ':code',
    component: ErrorComponent,
    data: {
      title: 'messages.error'
    }
  },
  {
    path: '**',
    component: ErrorComponent,
    data: {
      title: 'messages.error'
    }
  },
];

@NgModule({
  declarations: [
    ErrorComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    ReactiveFormsModule,
    RouterModule.forChild(routes)
  ],
  providers: []
})
export class ErrorModule {
}
