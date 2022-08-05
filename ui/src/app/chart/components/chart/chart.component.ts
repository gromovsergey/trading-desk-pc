import {Component, Input, ViewChild, OnInit} from '@angular/core';
import {ChartService} from '../../services/chart.service';
import {
  chartMetrics,
  chartObjectsLineItem,
  chartObjectsFlight,
  chartTypes,
  chartPeriods,
  moment,
  dateFormatParse,
  expandedChartMetrics
} from '../../../common/common.const';
import {L10nChartTypes, L10nChartPeriods, L10nChartMetrics, L10nChartObjects} from '../../../common/L10n.const';
import {L10nStatic} from '../../../shared/static/l10n.static';
import {ChartDataSets, ChartOptions} from 'chart.js';
import {BaseChartDirective, Label} from 'ng2-charts';
import {CHART_COLORS} from '../../const';
import * as chartJsPluginZoom from 'chartjs-plugin-zoom';


@Component({
  selector: 'ui-chart',
  templateUrl: './chart.component.html',
  styleUrls: ['./chart.component.scss']
})
export class ChartComponent implements OnInit {

  @Input() flightId: number;
  @Input() lineItemId: number;
  @ViewChild(BaseChartDirective, {static: true}) chart: BaseChartDirective;
  chartMetrics;
  chartTypes = chartTypes;
  chartPeriods = chartPeriods;
  chartObjects;
  params = {
    chartObject: null,
    entityId: null,
    chartMetric: 'IMPS',
    dateStart: null,
    dateEnd: null,
    chartType: 'DAILY',
    selectedPeriod: 'ALL'
  };
  wait = false;
  loadFailed = false;
  _seriesData: any;
  L10nChartTypes = L10nChartTypes;
  L10nChartMetrics = L10nChartMetrics;
  L10nChartObjects = L10nChartObjects;
  L10nChartPeriods = L10nChartPeriods;
  lineChartPlugins = [chartJsPluginZoom];
  lineChartData: ChartDataSets[] = [];
  lineChartColors = CHART_COLORS;
  lineChartLabels: Label[] = [];
  lineChartZoomed: boolean;
  lineChartResetZoom: any;
  lineChartOptions: (ChartOptions) = {
    responsive: true,
    maintainAspectRatio: false,
    scales: {
      xAxes: [{
        id: 'x-axis-0',
        type: 'time',
        distribution: 'linear'
      }],
      yAxes: [{}]
    },
    plugins: {
      zoom: {
        pan: {
          enabled: true,
          mode: 'xy',
          onPanComplete: ({chart}) => {
            this.lineChartZoomed = true;
            this.lineChartResetZoom = chart.resetZoom.bind(this);
          }
        },
        zoom: {
          enabled: false,
          mode: 'xy',
          onZoomComplete: ({chart}) => {
            this.lineChartZoomed = true;
            this.lineChartResetZoom = chart.resetZoom.bind(this);
          }
        }
      }
    }
  };

  set seriesData(data: any) {
    this._seriesData = data;
    this.renderPlot();
    this.wait = false;
  }

  get seriesData(): any {
    return this._seriesData;
  }

  get yLabel(): string {
    return L10nStatic.translate(L10nChartMetrics[this.params.chartMetric]);
  }

  constructor(private chartService: ChartService) {
  }

  ngOnInit(): void {
    this.initType();
    this.loadSeries();
  }

  initType(): void {
    if (this.lineItemId !== undefined) {
      this.params.entityId = +this.lineItemId;
      this.params.chartObject = 'LINE_ITEM';
      this.chartObjects = chartObjectsLineItem;
    } else {
      this.params.chartObject = 'FLIGHT';
      this.params.entityId = +this.flightId;
      this.chartObjects = chartObjectsFlight;
    }
    this.chartMetrics = expandedChartMetrics;
  }

  loadSeries(): void {
    this.wait = true;
    this.loadFailed = false;

    this.chartService.getSeries(this.getParams()).then(seriesData => {
      this.seriesData = seriesData;
    });
  }

  paramsChange(): void {
    if (this.params.chartObject === 'FLIGHT' || this.params.chartObject === 'LINE_ITEM') {
      this.chartMetrics = expandedChartMetrics;
    } else {
      this.chartMetrics = chartMetrics;
      if (chartMetrics.findIndex(f => f === this.params.chartMetric) < 0) {
        this.params.chartMetric = 'IMPS';
      }
    }
    this.loadSeries();
  }

  getParams(): any {
    const params = Object.assign({}, this.params);
    switch (this.params.selectedPeriod) {
      case 'LW':
        params.dateStart = moment().subtract(1, 'week').startOf('week').format(dateFormatParse);
        params.dateEnd = moment().subtract(1, 'week').endOf('week').format(dateFormatParse);
        break;
      case 'LM':
        params.dateStart = moment().subtract(1, 'month').startOf('month').format(dateFormatParse);
        params.dateEnd = moment().subtract(1, 'month').endOf('month').format(dateFormatParse);
        break;
      case 'TW':
        params.dateStart = moment().startOf('week').format(dateFormatParse);
        params.dateEnd = moment().endOf('week').format(dateFormatParse);
        break;
      case 'TM':
        params.dateStart = moment().startOf('month').format(dateFormatParse);
        params.dateEnd = moment().endOf('month').format(dateFormatParse);
        break;
      default:
        delete params.dateStart;
        delete params.dateEnd;
    }
    return params;
  }

  renderPlot(): void {
    try {
      if (this.seriesData) {
        if (this.seriesData.x && this.seriesData.x.length) {
          this.lineChartLabels = this.seriesData.x.map(timestamp => new Date(timestamp).toLocaleDateString(L10nStatic.getLocale()));
        }
        if (this.seriesData.y && this.seriesData.y.length) {
          this.lineChartData = this.seriesData.y.map(item => ({
              data: item.points,
              label: item.label
            }));
        }
        this.lineChartOptions = Object.assign({}, this.lineChartOptions, {
          scales: {
            yAxes: [
              {
                id: 'y-axis-0',
                position: 'left',
                type: 'linear',
                scaleLabel: {
                  display: true,
                  labelString: this.yLabel,
                  fontSize: 18
                }
              }
            ]
          }
        });
      } else {
        this.lineChartData = null;
      }
    } catch (e) {
      this.loadFailed = true;
    }
  }
}
