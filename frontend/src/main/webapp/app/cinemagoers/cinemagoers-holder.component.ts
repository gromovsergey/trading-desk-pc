import { Component, OnInit, AfterViewInit, OnDestroy, ViewChildren, QueryList } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { RouterLink, ActivatedRoute } from '@angular/router';
import { CinemagoersChartComponent } from './cinemagoers-chart.component';
import { CINEMAGOERS_CATEGORIES } from './cinemagoers.const';
import { splitArray } from '../common/utilities';
import { jQuery as $ } from '../common/common.const';
import * as util from '../common/utilities';

@Component({
  selector: 'ui-cinemagoers-holder',
  templateUrl: 'cinemagoers-holder.component.html',
})
export class CinemagoersHolderComponent implements OnInit, AfterViewInit, OnDestroy {
  public category: any;
  public charts: Array<[string]> = [];
  public allCharts: Array<[string]>;
  private count = 2;
  @ViewChildren(CinemagoersChartComponent) chartComponents: QueryList<CinemagoersChartComponent>;

  constructor(route: ActivatedRoute) {
    let segments = route.snapshot.url;
    this.category = CINEMAGOERS_CATEGORIES
      .find(element => element.id===segments[segments.length-1].path);
  }

  ngOnInit() {
    this.allCharts = splitArray(this.category.charts, this.count);
    this.charts.push(this.allCharts.shift());
  }

  ngAfterViewInit() {
    util.onWindowEvent('scroll.cinemagoers', () => {
      let element = document.documentElement;
      let isVisible = element.scrollHeight - element.scrollTop === element.clientHeight;
      isVisible && this.nextPage();
    })
  }

  ngOnDestroy() {
    util.offWindowEvent('.cinemagoers');
  }

  public nextPage(): void {
    this.chartComponents.last.wait || this.allCharts.length && this.charts.push(this.allCharts.shift());
  }
}
