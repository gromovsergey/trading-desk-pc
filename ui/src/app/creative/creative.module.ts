import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {SharedModule} from '../shared/shared.module';
import {CreativePreviewComponent} from './components/creative-preview/creative-preview.component';
import {CREATIVE_SERVICES} from './services';
import {CREATIVE_COMPONENTS} from './components';
import {MAT_COLOR_FORMATS, NGX_MAT_COLOR_FORMATS, NgxMatColorPickerModule} from '@angular-material-components/color-picker';
import { ShowWarningComponent } from './components/creative-options/show-warning/show-warning.component';

@NgModule({
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    SharedModule,
    NgxMatColorPickerModule
  ],
  providers: [
    ...CREATIVE_SERVICES,
    { provide: MAT_COLOR_FORMATS, useValue: NGX_MAT_COLOR_FORMATS }
  ],
  declarations: [
    ...CREATIVE_COMPONENTS,
    ShowWarningComponent,
  ],
  exports: [
    CreativePreviewComponent
  ]
})
export class CreativeModule {
}

