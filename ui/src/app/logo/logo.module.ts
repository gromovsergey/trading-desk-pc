import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {LogoComponent} from "./logo.component";

@NgModule({
  imports: [
    CommonModule,
  ],
  providers: [],
  declarations: [
    LogoComponent
  ],
  exports: [
    LogoComponent
  ]
})
export class LogoModule {
}

