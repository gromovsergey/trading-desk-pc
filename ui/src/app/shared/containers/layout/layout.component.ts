import {Component, OnDestroy} from '@angular/core';
import {BreakpointObserver, BreakpointState} from '@angular/cdk/layout';
import {takeUntil, map} from 'rxjs/operators';
import {Subject} from 'rxjs';

@Component({
  selector: 'ui-layout',
  templateUrl: './layout.component.html',
  styleUrls: ['./layout.component.scss']
})
export class LayoutComponent implements OnDestroy {

  menuOpened = true;
  isMobile: boolean;
  private destroy$ = new Subject<boolean>();

  constructor(breakpointObserver: BreakpointObserver) {
    this.isMobile = breakpointObserver.isMatched('(max-width: 435px)');
    breakpointObserver.observe('(max-width: 435px)').pipe(
      takeUntil(this.destroy$),
      map((match: BreakpointState) => match.matches)
    ).subscribe((matches: boolean) => {
      this.isMobile = matches;
      this.menuOpened = !this.isMobile;
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.unsubscribe();
  }
}
