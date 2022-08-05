import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {SharedModule} from '../shared/shared.module';
import {ConversionRoutingModule} from './conversion-routing.module';
import {CONVERSION_SERVICES} from './services';
import {CONVERSION_COMPONENTS} from './components';
import {ConversionPreviewComponent} from './components/conversion-preview/conversion-preview.component';

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    FormsModule,
    ConversionRoutingModule
  ],
  providers: [
    ...CONVERSION_SERVICES,
  ],
  declarations: [
    ...CONVERSION_COMPONENTS
  ],
  exports: [
    ConversionPreviewComponent
  ]
})
export class ConversionModule {
}

