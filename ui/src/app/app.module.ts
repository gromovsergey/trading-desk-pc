import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
// module
import {AppRoutingModule} from './app-routing.module';
import {HttpClientModule} from '@angular/common/http';
import {FormsModule} from '@angular/forms';
import {SharedModule} from './shared/shared.module';
// components
import {AppComponent} from './app.component';
import {DefaultPathComponent} from './common/default_path.component';
import {DashboardComponent} from './common/dashboard.component';
import {LayoutEmptyComponent} from './common/layout_empty.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {QuickSearchService} from './shared/services/quick-search.service';
import {COMMON_SERVICES} from './common/services';
import {MAT_DATE_LOCALE} from '@angular/material/core';


@NgModule({
  declarations: [
    AppComponent,
    DefaultPathComponent,
    DashboardComponent,
    LayoutEmptyComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    SharedModule,
    BrowserAnimationsModule,
  ],
  providers: [
    ...COMMON_SERVICES,
    QuickSearchService,
    {provide: MAT_DATE_LOCALE, useValue: 'ru-RU'},
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
