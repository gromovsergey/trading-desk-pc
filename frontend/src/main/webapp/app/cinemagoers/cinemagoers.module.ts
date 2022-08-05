import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SharedModule } from '../shared/shared.module';

import { CinemagoersRoutingModule } from './cinemagoers-routing.module';
import { CinemagoersService } from './cinemagoers.service';
import { CinemagoersComponent } from './cinemagoers.component';
import { CinemagoersChartComponent } from './cinemagoers-chart.component';
import { CinemagoersHolderComponent } from './cinemagoers-holder.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    SharedModule,
    CinemagoersRoutingModule
  ],
  declarations: [
    CinemagoersComponent,
    CinemagoersChartComponent,
    CinemagoersHolderComponent
  ],
  providers: [ CinemagoersService ]
})
export class CinemagoersModule { }
