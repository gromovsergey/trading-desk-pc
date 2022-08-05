import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {RouterModule} from '@angular/router';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
// shared
import {SHARED_COMPONENTS} from './components';
import {SHARED_CONTAINERS} from './containers';
import {SHARED_PIPES} from './pipes';
import {SHARED_DIRECTIVES} from './directives';
import {SHARED_SERVICES} from './services';
// services
import {AuthInterceptorService} from './services/auth-interceptor.service';
// components
import {PopupComponent} from './popup.component';
import {FileUploadComponent} from './file_upload.component';
import {FlightAttachmentUploadComponent} from './flight_attachement_upload.component';
import {DateRangeComponent} from './components/date-range/date-range.component';
// material
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatMenuModule} from '@angular/material/menu';
import {MatListModule} from '@angular/material/list';
import {MatSelectModule} from '@angular/material/select';
import {MatExpansionModule} from '@angular/material/expansion';
import {MatCardModule} from '@angular/material/card';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {MatTableModule} from '@angular/material/table';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {MatRadioModule} from '@angular/material/radio';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {DateAdapter, MatNativeDateModule} from '@angular/material/core';
import {MatDialogModule} from '@angular/material/dialog';
import {LogoModule} from "../logo/logo.module";
import {MatIconModule} from "@angular/material/icon";
import { ButtonListenerDirective } from './components/option-transfer/button-listener.directive';
import { SearchMultiselectListComponent } from './components/search-multiselect-list/search-multiselect-list.component';
import { RenderComponent } from './components/render/render.component';
import {NgMultiSelectDropDownModule} from "ng-multiselect-dropdown";
import { ShowInfoComponent } from './components/show-info/show-info.component';
import {CustomMatRangeService} from "./components/date-range/custom-mat-range.service";


const exported = [
  ...SHARED_PIPES,
  ...SHARED_CONTAINERS,
  ...SHARED_COMPONENTS,
  ...SHARED_DIRECTIVES,
];

const material = [
  MatInputModule,
  MatFormFieldModule,
  MatButtonModule,
  MatCheckboxModule,
  MatProgressSpinnerModule,
  MatToolbarModule,
  MatSidenavModule,
  MatMenuModule,
  MatListModule,
  MatSelectModule,
  MatExpansionModule,
  MatCardModule,
  MatSnackBarModule,
  MatTableModule,
  MatSlideToggleModule,
  MatRadioModule,
  MatTooltipModule,
  MatDatepickerModule,
  MatNativeDateModule,
  MatDialogModule,
];


@NgModule({
    imports: [
        FormsModule,
        CommonModule,
        RouterModule,
        HttpClientModule,
        ...material,
        ReactiveFormsModule,
        LogoModule,
        MatIconModule,
        NgMultiSelectDropDownModule.forRoot(),
    ],
  providers: [
      ...SHARED_SERVICES,
      { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptorService, multi: true },
      { provide: DateAdapter, useClass: CustomMatRangeService },
  ],
  declarations: [
    PopupComponent,
    FileUploadComponent,
    FlightAttachmentUploadComponent,
    DateRangeComponent,
    ...exported,
    ButtonListenerDirective,
    SearchMultiselectListComponent,
    RenderComponent,
    ShowInfoComponent
  ],
    exports: [
        PopupComponent,
        FileUploadComponent,
        FlightAttachmentUploadComponent,
        DateRangeComponent,
        ...exported,
        ...material,
        SearchMultiselectListComponent,
        RenderComponent
    ]
})
export class SharedModule {
}
