import { NgModule }                from '@angular/core';
import { RouterModule, Routes }    from '@angular/router';

import { FlightComponent }     from './flight.component';
import { FlightEditComponent } from './flight_edit.component';

const ROUTES: Routes = [
  { path: 'add',      component: FlightEditComponent },
  { path: ':id/edit', component: FlightEditComponent },
  { path: ':id', component: FlightComponent },
  { path: ':id/lineitem/add', component: FlightEditComponent }
];

@NgModule({
  imports: [
    RouterModule.forChild(ROUTES)
  ],
  exports: [ RouterModule ]
})

export class FlightRoutingModule {}