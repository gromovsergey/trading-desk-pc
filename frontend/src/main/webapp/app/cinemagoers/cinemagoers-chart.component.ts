import { Component, Input, OnInit, AfterViewInit, OnDestroy,
  ViewChild, ElementRef, Output, EventEmitter } from '@angular/core';
import { NgIf, NgClass } from '@angular/common';
import { RouterLink } from '@angular/router';
import { NgModel } from '@angular/forms';
import { LoadingComponent } from '../shared/loading.component';
import { CinemagoersService } from './cinemagoers.service';
import { jQuery as $, guid } from '../common/common.const';
import { CINEMAGOERS_CHART_CONFIG } from './cinemagoers.const';
import ChartDefaultConfig from '../common/chart-utilities';

@Component({
  selector: 'ui-cinemagoers-chart',
  templateUrl: 'cinemagoers-chart.component.html'
})
export class CinemagoersChartComponent implements OnInit, AfterViewInit, OnDestroy {
  @Input() chartName = 'social_relation';
  @ViewChild('imageLink') imageLink: ElementRef;
  @Output() edit: EventEmitter<any> = new EventEmitter(true);
  public containerId = guid();
  public chart: any;
  public comments: string;
  public wait = true;
  public error: any;
  public config: any;

  constructor(private service: CinemagoersService) { }

  ngOnInit() {
    this.config = CINEMAGOERS_CHART_CONFIG[this.chartName];
    this.comments = this.config.comments;
  }

  ngAfterViewInit() {
    this.chart = new ChartDefaultConfig[this.config.template]._chart(this.containerId);
    this.service.getData(this.chartName)
      .then((data) => {
        this.chart.renderChart(data, this.config)
          .then((result) => {
            this.error = undefined;
            this.wait = false;
          })
          .catch((e) => {
            this.error = e;
            this.wait = false;
          })
      });
  }

  ngOnDestroy() {
    this.chart.destroy();
  }

  public toggleComments(event: any): void {
    $(`#${this.containerId}`).parent()
      .find('div.cinemagoers-chart-comments, .cinemagoers-chart-comments-control').toggle()
      .find('textarea').prop('readonly', true);
    $(event.target).toggleClass('active');
  }

  public editComments(event: any): void {
    $(`#${this.containerId}`).parent()
      .find('div.cinemagoers-chart-comments textarea')
      .prop('readonly', function (idx, val) {
        return !val;
      });
  }

  public onCommentsChange(event: any) {
    this.edit.emit({
      comments: this.comments
    });
  }
}
