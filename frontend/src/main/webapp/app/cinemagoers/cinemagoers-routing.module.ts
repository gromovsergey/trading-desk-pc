import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CinemagoersComponent } from './cinemagoers.component';
import { CinemagoersHolderComponent } from './cinemagoers-holder.component';

const ROUTES: Routes = [
  { path: '', component: CinemagoersComponent },
  { path: ':id', component: CinemagoersHolderComponent }
];

@NgModule({
  imports: [
    RouterModule.forChild(ROUTES)
  ],
  exports: [ RouterModule ]
})

export class CinemagoersRoutingModule {}
