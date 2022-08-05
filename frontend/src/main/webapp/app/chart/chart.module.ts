import { NgModule }     from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule }  from '@angular/forms';

import { SharedModule }   from '../shared/shared.module';
import { ChartService }   from './chart.service';
import { ChartComponent } from './chart.component';

@NgModule({
  imports:      [
    CommonModule,
    FormsModule,
    SharedModule
  ],
  providers:    [
    ChartService
  ],
  declarations: [
    ChartComponent
  ],
  exports: [
    ChartComponent
  ]
})

export class ChartModule {}

