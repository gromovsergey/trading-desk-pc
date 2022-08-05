import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {FlightComponent} from './components/flight/flight.component';
import {FlightEditComponent} from './components/flight-edit/flight-edit.component';

const ROUTES: Routes = [
  {path: 'add', component: FlightEditComponent},
  {path: ':id/edit', component: FlightEditComponent},
  {path: ':id', component: FlightComponent},
  {path: ':id/lineitem/add', component: FlightEditComponent}
];

@NgModule({
  imports: [
    RouterModule.forChild(ROUTES)
  ],
  exports: [RouterModule]
})
export class FlightRoutingModule {
}
