import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { enableProdMode }         from '@angular/core';
import { AppModule }              from './app.module';

if (process.env._USE_PROD_MODE_) {
  enableProdMode();
}

platformBrowserDynamic().bootstrapModule(AppModule).catch(e => console.error(e));
