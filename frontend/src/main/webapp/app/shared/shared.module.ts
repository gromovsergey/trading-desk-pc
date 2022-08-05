import { NgModule }     from '@angular/core';
import { FormsModule }  from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { FileService }                     from './file.service';
import { IconComponent }                   from './icon.component';
import { IconRequiredComponent }           from './required_icon.component';
import { LoadingComponent }                from './loading.component';
import { PageComponent }                   from './page.component';
import { CellComponent }                   from './cell.component';
import { DisplayStatusButtonComponent }    from './display_status_button.component';
import { DisplayStatusToggleComponent }    from './display_status_toggle.component';
import { DropdownButtonComponent }         from './dropdown_button.component';
import { PanelComponent }                  from './panel.component';
import { HintComponent }                   from './hint.component';
import { PopupComponent }                  from './popup.component';
import { FileUploadComponent }             from './file_upload.component';
import { AccountDocumentsComponent }       from './account_documents.component';
import { AccountDocumentUploadComponent }  from './account_document_upload.component';
import { ChannelReportUploadComponent }    from './channel_report_upload.component';
import { FlightAttachmentUploadComponent } from './flight_attachement_upload.component';
import { OptionTransferComponent }         from './option_transfer.component';
import { DisplayStatusDirective }          from './display_status.directive';
import { DatetimeDirective }               from './datetime.directive';
import { DateRangeComponent }              from './date_range.component';
import { ReplacePipe }                     from "./replace.pipe";
import { ChannelTreeComponent }            from "./channel_tree.component";
import {ChannelDynamicLocalizationComponent} from "./channel_dynamic_localization.component";

@NgModule({
  imports:      [
    FormsModule,
    CommonModule,
    RouterModule
  ],
  providers:    [
    FileService
  ],
  declarations: [
    IconComponent,
    IconRequiredComponent,
    LoadingComponent,
    PageComponent,
    CellComponent,
    DisplayStatusButtonComponent,
    DisplayStatusToggleComponent,
    DropdownButtonComponent,
    PanelComponent,
    HintComponent,
    PopupComponent,
    FileUploadComponent,
    AccountDocumentsComponent,
    AccountDocumentUploadComponent,
    ChannelReportUploadComponent,
    FlightAttachmentUploadComponent,
    OptionTransferComponent,
    ChannelTreeComponent,
    ChannelDynamicLocalizationComponent,
    DisplayStatusDirective,
    DatetimeDirective,
    DateRangeComponent,
    ReplacePipe
  ],
  exports: [
    IconComponent,
    IconRequiredComponent,
    LoadingComponent,
    PageComponent,
    CellComponent,
    DisplayStatusButtonComponent,
    DisplayStatusToggleComponent,
    DropdownButtonComponent,
    PanelComponent,
    HintComponent,
    PopupComponent,
    FileUploadComponent,
    AccountDocumentsComponent,
    AccountDocumentUploadComponent,
    ChannelReportUploadComponent,
    FlightAttachmentUploadComponent,
    OptionTransferComponent,
    ChannelTreeComponent,
    ChannelDynamicLocalizationComponent,
    DisplayStatusDirective,
    DatetimeDirective,
    DateRangeComponent,
    ReplacePipe
  ]
})

export class SharedModule {}
