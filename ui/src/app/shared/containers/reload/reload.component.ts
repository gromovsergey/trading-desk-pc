import {Component} from '@angular/core';
import {Router} from '@angular/router';
import {SESSION_LAST_URL} from '../../../const';

@Component({
  selector: 'ui-redirect',
  template: `{{'messages.redirect' | translate}}`
})
export class ReloadComponent {

  constructor(private router: Router) {
    const route = window.sessionStorage.getItem(SESSION_LAST_URL) || '/';
    this.router.navigateByUrl(route).catch(() => console.error('Reload page error'));
  }
}
