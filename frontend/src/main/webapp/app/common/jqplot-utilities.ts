import { jQuery as $ } from './common.const';
import * as util from './utilities';
import { ChartRenderer } from './chart';

const JQPLOT_HEIGHT = 400;

class jqPlotChart implements ChartRenderer {
  private id: string;
  private chart: any;
  private options: any;

  constructor(containerId: string) {
    this.id = containerId;
  }

  public renderChart(data: Array<[number]>, config) {
    return new Promise((resolve, reject) => {
      $.jqplot.config.enablePlugins = true;
      $.jqplot.config.defaultHeight = this.getHeight(data, config);
      this.options = this.getOptions(config);
      this.chart = $.jqplot(this.id, this.composePlotData(data, this.options), this.options);
      this.chart.postDrawHooks.add(this.addHandlers.bind(this));
      this.addHandlers();
      this.onResize();
      resolve();
    });
  }

  public toImage(event) {
    try {
      $(event.target).closest('a').attr('href', $(`#${this.id}`).jqplotToImageStr());
    } catch (e) {
      console.log(e);
    }
  }

  private getHeight(data:  Array<[number]>, config: any): number {
    let count = {'BAR_HORIZONTAL':17, 'LINE_TIME':15};
    return Math.ceil(Math.max(data.length, data[0].length)/count[config.template]) * JQPLOT_HEIGHT || JQPLOT_HEIGHT;
  }

  public destroy() {
    util.offWindowEvent(`resize.jqplot_${this.id}`);
    this.chart.destroy();
  }

  private addHandlers(): void {
    try {
      let instance = this;
      $(`#${this.id} tr.jqplot-table-legend`).on('click', function () {
        instance.toggleSeries($(this));
      });
      $(`#${this.id}`).on('jqplotDataClick', (event, seriesIndex) => {
        $(`#${this.id} table.jqplot-table-legend`).children().toArray().forEach(
          (elem, idx) => idx===seriesIndex || this.toggleSeries($(elem))
        );
      });
    } catch (e) {
      console.log(e);
    }
  }

  private toggleSeries(element: any): void {
    let index = element.index();
    let state = element.hasClass('disabled');
    //this.chart.series[index].plugins.pointLabels.show = state;
    this.chart.series[index].showHighlight = state;
    this.chart.drawSeries({showLine: state, showHighlight: state}, index);
    element.toggleClass('disabled');
  }

  private onResize(): void {
    //this.replot(this.getReplotOptions());
    //let initOptions = util.mergeObjects(this.chart.options);
    util.onWindowEvent(`resize.jqplot_${this.id}`, () => {
      try {
        //this.chart.replot(this.getReplotOptions(initOptions));
        //this.chart.resetAxes = true;
        //this.chart.replot(this.options);
        this.chart.replot();
      } catch (e) {}
    }, 200);
  }

  private getOptions(config: any): any {
      let options = {};
      if (config.template=='BAR_VERTICAL') {
          let longTicks = Math.max.apply(null, config.options.axes.xaxis.ticks.map(elem => elem.length)) > 20;
          $.jqplot.config.defaultHeight += longTicks ? JQPLOT_HEIGHT : 0;
          options = {
              axes: {
                  xaxis: {
                      tickOptions: {
                          angle: longTicks ? -80 : -30
                      }
                  }
              }
          };
      }
      return util.mergeObjects({_id: config.template}, DefaultConfig[config.template], config.options, options);
  }

  /*private replot(options?: any): void {
    //options = options && util.mergeObjects(this.options, options) || this.options;
    //this.chart.resetAxes = true;
    this.chart.replot(options);
  }*/

  /*private getReplotOptions(initOptions: any): any {
    let minWidth = 400;
    return {
      axes: {
        xaxis: {
          tickOptions: {
            angle: util.traverse(initOptions, 'axes.xaxis.tickOptions.angle') ||
              ($(`${this.chart.targetId}`).width() < minWidth ? -30 : 0)
          }
        }
      }
    };
  }*/

  private composePlotData(data: Array<[number]>, options: any) {
    return data
      .filter((elem) => elem.filter((elem) => !!elem || elem===0).length)
      .map(elem => elem.map((elem, idx) =>
        ({
          BAR_VERTICAL:   () => [idx+1, elem],
          BAR_HORIZONTAL: () => [elem, idx+1],
          LINE_TIME:      () => [this.getXValues(options)[idx], elem],
          DONUT:          () => {
            options.seriesDefaults.rendererOptions.dataLabels.push(`${options.legend.labels[idx]} ${elem}%`);
            return [idx+1, elem];
          }
        })[options._id]()
      ));
  }

  private getXValues(options: any): Array<any> {
    return util.traverse(options, 'axes.xaxis.ticks') || [];
  }
}

const AXIS_COLOR = '#444';
const BAR_COLORS = ['#0095dd', '#7eb2e6'];
const LINE100_COLOR = 'orange';
export const GOOGLE_COLOR = '#aac14a';
export const APPLE_COLOR = '#cdcfd1';

const CATEGORY_AXIS = {
  renderer: $.jqplot.CategoryAxisRenderer,
  rendererOptions: {
    baselineWidth: 1.5,
    baselineColor: AXIS_COLOR,
    drawBaseline: true
  },
  label: '',
  tickRenderer: $.jqplot.CanvasAxisTickRenderer,
  tickOptions: {
    show: true
  }
};

const VALUE_AXIS = {
  //pad: 1.5,
  renderer: $.jqplot.LinearAxisRenderer,
  rendererOptions: {
    drawBaseline: false
  },
  labelRenderer: $.jqplot.CanvasAxisLabelRenderer,
  label: '',
  tickOptions: {
    show: false,
    formatString: '%d%%'    // persent
    /*formatString: '%d'*/
  },
  min: 0
};

const LINE100 = {
  show: true,
  objects: [{
    dashedHorizontalLine: {
      y: 100,
      lineWidth: 2,
      dashPattern: [2, 8],
      color: LINE100_COLOR,
      shadow: false,
      xOffset: 0
    }
  }, {
    dashedVerticalLine: {
      x: 100,
      lineWidth: 2,
      dashPattern: [2, 8],
      color: LINE100_COLOR,
      shadow: false,
      yOffset: 0
    }
  }]
};

const COMMON_OPTIONS = {
  _chart: jqPlotChart,
  target: {
    backgroundColor: 'transparent'
  },
  title: '',
  seriesDefaults: {
    pointLabels: {
      show: true
    }
  },
  axesDefaults: {
    tickOptions: {
      show: false,
      showMark: false
    }
  },
  axes: {
    xaxis: CATEGORY_AXIS,
    yaxis: VALUE_AXIS
  },
  grid: {
    drawBorder: false,
    shadow: false,
    drawGridlines: false,
    background: 'transparent'
  },
  legend: {
    show: false
  },
  cursor: {
    show: false
  },
  highlighter: {
    show: false
  },
  canvasOverlay: LINE100
};

export namespace DefaultConfig {
  export const BAR_VERTICAL = util.mergeObjects(COMMON_OPTIONS, {
    seriesColors: BAR_COLORS,
    seriesDefaults: {
      renderer: $.jqplot.BarRenderer,
      pointLabels: {
        show: true,
        location: 's',
        //formatString: '%s',
        //formatter: (format, val) => `${val}<small>%</small>`,
        //escapeHTML: false
      },
      rendererOptions: {
        barPadding: 0,
        barMargin: 5,
        shadowAlpha: 0,
        highlightMouseOver: false
      }
    },
    axes: {
      xaxis: {
        tickOptions: {
          angle: -30
        }
      }
    },
    highlighter: {
      show: true,
      showMarker: true,
      tooltipLocation: 'n',
      tooltipContentEditor: (value, seriesIndex, pointIndex, jqPlot) =>
        `${jqPlot.options.axes.xaxis.ticks[pointIndex]}: <b>${jqPlot.data[seriesIndex][pointIndex][1]}</b>%`
    }
  });

  export const BAR_HORIZONTAL = util.mergeObjects(COMMON_OPTIONS, BAR_VERTICAL, {
    seriesDefaults: {
      rendererOptions: {
        barDirection: 'horizontal'
      },
      pointLabels: {
        location: 'e'
      }
    },
    axes: {
      xaxis: VALUE_AXIS,
      yaxis: CATEGORY_AXIS
    },
    highlighter: {
      show: true,
      showMarker: true,
      tooltipLocation: 'n',
      tooltipContentEditor: (value, seriesIndex, pointIndex, jqPlot) =>
        `${jqPlot.options.axes.yaxis.ticks[pointIndex]}: <b>${jqPlot.data[seriesIndex][pointIndex][0]}</b>%`
    }
  });

  export const DONUT = util.mergeObjects(COMMON_OPTIONS, {
    seriesDefaults: {
      renderer: $.jqplot.DonutRenderer,
      rendererOptions: {
        sliceMargin: 1,
        startAngle: -90,
        dataLabelPositionFactor: 1.7,
        showDataLabels: true,
        dataLabels: [],
        shadowAlpha: 0,
        highlightMouseOver: false
      }
    },
    axesDefaults: {
      rendererOptions: {
        show: false
      },
      tickOptions: {
        show: false
      }
    },
    canvasOverlay: {
      show: false
    }
  });

  export const LINE_TIME = util.mergeObjects(COMMON_OPTIONS, {
    seriesDefaults: {
      renderer: $.jqplot.LineRenderer,
      rendererOptions: {
        shadowAlpha: 0,
        shadowOffset: 0,
        shadowAngle: 0
      },
      lineWidth: 4,
      smooth: true,
      showMarker: false,
      pointLabels: {
        show: false
      }
    },
    axes: {
      xaxis: {
        renderer: $.jqplot.DateAxisRenderer,
        tickRenderer: $.jqplot.CanvasAxisTickRenderer,
        tickOptions: {
          formatString: '%b %e',
          angle: -30
        }
      },
      yaxis: {
        rendererOptions: {
          baselineWidth: 1.5,
          baselineColor: AXIS_COLOR,
          drawBaseline: true
        },
        label: '',
        tickRenderer: $.jqplot.CanvasAxisTickRenderer,
        tickOptions: {
          show: true,
          //formatString: '%d%%'    // persent
        }
      }
    },
    grid: {
      drawBorder: true,
      borderWidth: 0.5,
      borderColor: '#ddd',
      drawGridlines: true,
      gridLineColor: '#ddd'
    },
    legend: {
       show: true,
       placement: 'outsideGrid',
       location: 'ne',
       /*renderer: $.jqplot.EnhancedLegendRenderer,
       rendererOptions: {
         seriesToggle: 'fast',
         seriesToggleReplot: true
       }*/
    },
    highlighter: {
      show: true,
      sizeAdjust: 10,
      tooltipOffset: 10,
      tooltipLocation: 'n',
      tooltipContentEditor: (value, seriesIndex, pointIndex, jqPlot) =>
        `${jqPlot.options.legend.labels[seriesIndex]}, ${value.replace(/\, ?([\d]+)\%?$/, ': <b>$1</b>%')}`
    }
  });
}
