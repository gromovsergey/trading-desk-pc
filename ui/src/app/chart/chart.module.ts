import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {SharedModule} from '../shared/shared.module';
import {ChartsModule} from 'ng2-charts';
import {CHART_COMPONENTS} from './components';
import {CHART_SERVICES} from './services';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    SharedModule,
    ChartsModule
  ],
  providers: [
    ...CHART_SERVICES
  ],
  declarations: [
    ...CHART_COMPONENTS
  ],
  exports: [
    ...CHART_COMPONENTS
  ]
})
export class ChartModule {
}

