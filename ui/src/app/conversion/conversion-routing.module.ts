import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ConversionEditComponent} from './components/conversion-edit/conversion-edit.component';

const ROUTES: Routes = [
  {path: 'add', component: ConversionEditComponent},
  {path: ':id/edit', component: ConversionEditComponent},
];

@NgModule({
  imports: [
    RouterModule.forChild(ROUTES)
  ],
  exports: [RouterModule]
})
export class ConversionRoutingModule {
}
