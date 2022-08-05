import { NgModule, LOCALE_ID }      from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpModule }    from '@angular/http';
import { FormsModule }   from '@angular/forms';
import 'rxjs/add/operator/toPromise';

import { SharedModule }     from './shared/shared.module';
import { AppRoutingModule } from './app-routing.module';

import { AuthGuard }                       from './common/auth.guard';
import { AuthService }                     from './common/auth.service';
import { CommonService }                   from './common/common.service';
import { DefaultPathComponent }            from './common/default_path.component';
import { FieldComponent }                  from './common/field.component';
import { NavbarComponent }                 from './common/navbar.component';
import { DashboardComponent }              from './common/dashboard.component';
import { LayoutComponent }                 from './common/layout.component';
import { LayoutEmptyComponent }            from './common/layout_empty.component';
import { LayoutCleanComponent }            from './common/layout_clean.component';
import { FooterComponent }                 from './common/footer.component';
import { MenuMainComponent }               from './common/menu_main.component';
import { LoginComponent }                  from './common/login.component';
import { ErrorComponent }                  from './common/error.component';
import { TestComponent }                   from './common/test.component';

import { AppComponent }                    from './app.component';

@NgModule({
    imports:      [
        BrowserModule,
        HttpModule,
        FormsModule,
        SharedModule,
        AppRoutingModule
    ],
    providers:    [ AuthGuard, AuthService, CommonService, {provide: LOCALE_ID, useValue: process.env._LANG_ || 'ru-RU' } ],
    declarations: [
        DefaultPathComponent,
        FieldComponent,
        NavbarComponent,
        DashboardComponent,
        LayoutComponent,
        LayoutEmptyComponent,
        LayoutCleanComponent,
        FooterComponent,
        MenuMainComponent,
        LoginComponent,
        ErrorComponent,
        TestComponent,

        AppComponent
    ],
    bootstrap: [ AppComponent ]
})

export class AppModule {}
