import { jQuery as $ } from './common.const';
import * as util from './utilities';
import { ChartRenderer } from './chart';

const CHART_LOADER_URL = 'https://www.gstatic.com/charts/loader.js';
const GEOCHART_HEIGHT = 600;

class GoogleGeoChart implements ChartRenderer {
  private id: string;
  private chart: any;
  private options: any;
  private dataTable: any;

  constructor(containerId: string) {
    this.id = containerId;
  }

  public renderChart(data: Array<[string, any, number]>, config) {
    return new Promise((resolve, reject) => {
      if (window['google']) {
        resolve(this.loadChart(data, config))
      } else {
        $.getScript(CHART_LOADER_URL)
          .done(() => {
            resolve(this.loadChart(data, config));
          })
          .fail((jqxhr) => {
            reject({message: `${jqxhr.status} ${jqxhr.statusText}`});
          });
      }
    });
  }

  public toImage(event) {
    try {
      $(event.target).closest('a').attr('href', this.chart.getImageURI());
    } catch (e) {
      console.log(e);
    }
  }

  public destroy() {
    util.offWindowEvent(`.google_${this.id}`);
    this.chart.clearChart();
  }

  private onResize(): void {
    util.onWindowEvent(`resize.google_${this.id}`, () => {
      try {
        this.setStyle('unset');
        this.draw({
          //width: Math.min(GEOCHART_WIDTH, $('ui-cinemagoers-chart[ng-reflect-chart-name=geo]').width() - 62)
          height: undefined
        });
      } catch (e) {}
    }, 200);
  }

  private loadChart(data, config): Promise<any> {
    return new Promise((resolve, reject) => {
      let ns = window['google'];
      ns.charts.load('current', {
        'packages': ['geochart'],
        'mapsApiKey': 'AIzaSyDi2t_PEpG9ertcQVKkmMKPboSGu-FdzhM',  // asar@mail.ru key
        'language': process.env._LANG_
      });
      ns.charts.setOnLoadCallback(() => {
        try {
          //this.setStyle();
          this.chart = new ns.visualization.GeoChart($(`#${this.id}`)[0]);
          this.options = this.getOptions(config);
          this.addTitle();
          ns.visualization.events.addListener(this.chart, 'ready', () => {
            this.showTooltips(data, this.options);
            this.setStyle();
            //this.positionTooltips();
          });
          ns.visualization.events.addListener(this.chart, 'select', () => {
            //this.positionTooltips();
          });
          this.dataTable = ns.visualization.arrayToDataTable([this.options._head, ...data]);
          this.draw();
          this.onResize();
          resolve();
        } catch (e) {
          reject(e);
        }
      });
    });
  }

  private draw(options?: any): void {
    options = options && util.mergeObjects(this.options, options) || this.options;
    this.chart.draw(this.dataTable, options);
  }

  private getOptions(config: any): any {
    return util.mergeObjects(DefaultConfig[config.template], config.options);
  }

  private showTooltips(data, options): void {
    this.chart.setSelection(options._selected.map(
      value => ({
        row: data.findIndex(elem => elem[0]===value)
      })
    ));
  }

  /*private setStyle(): void {
    $(`#${this.id}`).parent().addClass('google-geochart');
  }*/
  private setStyle(value?: string): void {
    let svg = $(`#${this.id} svg`);
    $(`#${this.id}>div>div:first-child`).css({
      width: function () {
        return value || svg.width() + 'px';
      },
      height: function () {
        return value || svg.height() + 'px';
      }
    });
  }

  private positionTooltips(): void {
      setTimeout(() => {
        let selected = $('circle + circle');
        $(`#${this.id} .google-visualization-tooltip`).css({
          top: function (idx, val) {
            //return parseInt(val) + (20*svg.height()/922) + 'px';
            return $(selected[idx]).attr('cy') + 'px';
          },
          left: function (idx, val) {
            //return parseInt(val) + (40*svg.width()/1478) + 'px';
            return $(selected[idx]).attr('cx') + 'px';
          }
        });
      }, 100);
  }

  private addTitle(): void {
    $(`#${this.id}`).before(`<div class="google-title">${this.options._title}</div>`);
  }
}

export namespace DefaultConfig {
  export const GOOGLE_GEOCHART = {
    _chart: GoogleGeoChart,
    region: 'RU',
    resolution: 'provinces',
    displayMode: 'markers',
    markerOpacity: 0.8,
    colorAxis: {colors: ['yellow', 'orange']},
    defaultColor: 'white',
    /*backgroundColor: {
      stroke: '#666',
      strokeWidth: 1
    },*/
    sizeAxis: {
      minSize: 3,
      maxSize: 10
    },
    legend: {textStyle: {color: '#666', fontSize: 14}},
    tooltip: {
      trigger: 'selection',
      isHtml: true
    },
    magnifyingGlass: {enable: true, zoomFactor: 3},
    //height: GEOCHART_HEIGHT,
    //keepAspectRatio: false
  }
}
