import {Component, EventEmitter, HostListener, OnDestroy, OnInit, Output} from '@angular/core';
import {LS_LANG, SESSION_LAST_URL} from '../../../const';
import {Router} from '@angular/router';
import {Subject} from 'rxjs';
import {debounceTime, filter, switchMap, takeUntil, tap} from 'rxjs/operators';
import {QuickSearchService} from '../../services/quick-search.service';
import {environment} from "../../../../environments/environment";

@Component({
  selector: 'ui-nav-top',
  templateUrl: './nav-top.component.html',
  styleUrls: ['./nav-top.component.scss']
})
export class NavTopComponent implements OnInit, OnDestroy {
  public hostName: 'Default' | 'Genius' | 'Pharmatic';
  @Output()
  menuClick = new EventEmitter<MouseEvent>();

  wait: boolean;
  quickSearch: string;
  showSearchField = false;
  quickSearchChange$: Subject<string> = new Subject();
  destroy$ = new Subject();
  searchData: any[];
  showQuickSearchData: boolean;

  get language(): string {
    return window.localStorage.getItem(LS_LANG);
  }

  set language(lang: string) {
    window.sessionStorage.setItem(SESSION_LAST_URL, this.router.url);
    window.setTimeout(() => {
      window.localStorage.setItem(LS_LANG, lang);
      this.router.navigateByUrl('/reload').catch(() => console.error('Language change error'));
    }, 200);
  }

  constructor(private router: Router, private service: QuickSearchService) {
    this.hostName = 'Default';
  }

  @HostListener('document:click', ['$event.target'])
  documentClick(target: HTMLElement): void {
    if (target.id !== 'quick-search') {
      window.setTimeout(() => {
        this.showQuickSearchData = false;
      }, 300);
    }
  }

  ngOnInit(): void {
    switch (window.location.protocol + '//' + window.location.hostname) {
      case environment.hostGenius:
        this.hostName = 'Genius';
        break;
      case environment.hostPharmatic:
        this.hostName = 'Pharmatic';
        break;
    }

    this.quickSearchChange$.pipe(
      takeUntil(this.destroy$),
      debounceTime(300),
      filter((value: string) => value && value.length > 2),
      tap(() => this.wait = true),
      switchMap(value => this.service.search(value))
    ).subscribe(res => {
      this.searchData = res;
      this.showQuickSearchData = true;
      this.wait = false;
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.unsubscribe();
  }

  getLink(type: string, id: number): any[] {
    switch (type) {
      case 'Agency':
        return ['/agency', id, 'advertisers'];
      case 'Advertiser':
        return ['/advertiser', id, 'flights'];
      case 'Flight':
        return ['/flight', id];
    }
  }

  getRowTitle(type: string): string {
    switch (type) {
      case 'Agency':
        return 'agencyAccount.agency';
      case 'Advertiser':
        return 'advertiserAccount.advertiser';
      case 'Flight':
        return 'flight.object';
    }
  }
}
