import { NgModule }     from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule }  from '@angular/forms';

import { SharedModule }                 from '../shared/shared.module';
import { CreativeService }              from './creative.service';
import { CreativeComponent }            from './creative.component';
import { CreativeEditComponent }        from './creative_edit.component';
import { CreativeLivePreview }          from './creative_live_preview.component';
import { CreativeOptionGroupComponent } from './creative_option_group.component';
import { CreativeOptionComponent }      from './creative_options.component';
import { CreativePreview }              from './creative_preview.component';

@NgModule({
  imports:      [
    CommonModule,
    RouterModule,
    FormsModule,
    SharedModule
  ],
  providers:    [
    CreativeService
  ],
  declarations: [
    CreativeComponent,
    CreativeEditComponent,
    CreativeLivePreview,
    CreativeOptionGroupComponent,
    CreativeOptionComponent,
    CreativePreview
  ],
  exports: [
    CreativePreview
  ]
})

export class CreativeModule {}

