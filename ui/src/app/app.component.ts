import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Data, NavigationEnd, Router} from '@angular/router';
import {Title} from '@angular/platform-browser';
import {Subject} from 'rxjs';
import {filter, switchMap, takeUntil, map, tap} from 'rxjs/operators';
import {L10nStatic} from './shared/static/l10n.static';
import {APP_POSTFIX, APP_POSTFIX_GENIUS, APP_POSTFIX_PHARMATIC, SESSION_LAST_URL} from './const';
import {environment} from "../environments/environment";
import {DOCUMENT} from "@angular/common";

@Component({
  selector: 'ui-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit, OnDestroy {

  private destroy$ = new Subject();

  constructor(private router: Router,
              private route: ActivatedRoute,
              private title: Title,
              @Inject(DOCUMENT) private _document: HTMLDocument) {
  }

  ngOnInit(): void {
    this.router.events.pipe(
      takeUntil(this.destroy$),
      filter(event => event instanceof NavigationEnd),
      switchMap(() => {
        const req = (child: ActivatedRoute): ActivatedRoute => (child && child.firstChild) ? req(child.firstChild) : child;
        return req(this.route.firstChild).data;
      }),
      tap(data => {
        if (!(data && data.skipUrl)) {
          sessionStorage.setItem(SESSION_LAST_URL, location.pathname + location.search);
        }
      }),
      map((data: Data) => data.title || '')
    ).subscribe((title: string) => {
      let postFix = APP_POSTFIX;
      const $el = this._document.getElementById('appFavicon')
      if (window.location.protocol + '//' + window.location.hostname === environment.hostGenius) {
        postFix = APP_POSTFIX_GENIUS
        this.setIcon($el, '/genius/newGenius/fav_pin.png');
        const head = this._document.getElementsByTagName('head')[0];
        const style = this._document.createElement('link');
        style.id = 'css-styling';
        style.rel = 'stylesheet';
        style.href = 'assets/genius/toggle.css';
        head.appendChild(style);
      }
      if (window.location.protocol + '//' + window.location.hostname === environment.hostPharmatic) {
        postFix = APP_POSTFIX_PHARMATIC
        this.setIcon($el, '/pharmatic/Pharmatic_logo_white.png');
      }
      this.title.setTitle(`${title ? L10nStatic.translate(title) + ' - ' : ''}${postFix}`);
    });
  }

  private setIcon = (element, path: string): void => {
    if (element) {
      element.setAttribute('href', `/assets/${path}`);
    }
  };

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.unsubscribe();
  }
}
