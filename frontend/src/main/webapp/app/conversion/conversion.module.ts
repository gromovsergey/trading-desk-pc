import { NgModule }     from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule }  from '@angular/forms';

import { SharedModule }                   from '../shared/shared.module';
import { ConversionRoutingModule }        from './conversion-routing.module';
import { ConversionService }              from './conversion.service';
import { ConversionEditComponent }        from './conversion_edit.component';
import { ConversionPreview }              from './conversion_preview.component';

@NgModule({
  imports:      [
    CommonModule,
    SharedModule,
    FormsModule,
    ConversionRoutingModule
  ],
  providers:    [
    ConversionService
  ],
  declarations: [
    ConversionEditComponent,
    ConversionPreview
  ],
  exports: [
    ConversionPreview
  ]
})

export class ConversionModule {}

