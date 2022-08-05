import { Component, Input, ElementRef, ViewChild, OnDestroy, OnChanges } from '@angular/core';

import { IconComponent }    from '../shared/icon.component';
import { LoadingComponent } from '../shared/loading.component';
import { ChartService }     from './chart.service';
import {
    jQuery, guid, chartMetrics, chartColors, chartObjectsLineItem,
    chartObjectsFlight, chartTypes, chartPeriods, moment, dateFormatParse, expandedChartMetrics
}                           from '../common/common.const';
import {
    L10nChartTypes, L10nChartPeriods, L10nChartMetrics, L10nChartObjects
}                           from '../common/L10n.const';

@Component({
    selector: 'ui-chart',
    templateUrl: 'chart.html'
})

export class ChartComponent implements OnChanges, OnDestroy{

    @Input() flightId: number;
    @Input() lineItemId: number;
    @ViewChild('chart') chartEl: ElementRef;

    private type: string;
    private plot: any;
    private jqPlot  = self['jQuery']['jqplot'];
    public chartMetrics;
    public chartTypes      = chartTypes;
    public chartPeriods    = chartPeriods;
    public chartObjects;
    public selectedPeriod: string  = 'ALL';
    public params  = {
        chartObject : null,
        entityId: null,
        chartMetric: 'IMPS',
        dateStart: null,
        dateEnd: null,
        chartType: 'DAILY',
    };

    public wait: boolean       = false;
    public loadFailed: boolean = false;

    private _seriesData: {};
    private seriesTooltips: any;

    public chartId: string     = guid();
    private onResize: boolean   = false;

    public L10nChartTypes = L10nChartTypes;
    public L10nChartMetrics = L10nChartMetrics;
    public L10nChartObjects = L10nChartObjects;
    public L10nChartPeriods = L10nChartPeriods;

    constructor(private chartService: ChartService){}

    ngOnInit(){
        this.initType().loadSeries();
        window.addEventListener('resize', this.onWindowResize.bind(this));
    }

    ngOnChanges(){
        this.initType().loadSeries();
    }

    ngOnDestroy(){
        if (this.plot !== undefined) {
            this.plot.destroy();
        }
        window.removeEventListener('resize', this.onWindowResize.bind(this));
    }

    set seriesData(data: any){
        this.wait   = false;
        if (data){
            this._seriesData = data;
            this.renderPlot();
        } else {
            this.loadFailed = true;
        }
    }

    get seriesData(){
        return this._seriesData;
    }

    private initType(){
        if (this.lineItemId !== undefined){
            this.type   = 'lineitem';
            this.params.entityId    = +this.lineItemId;
            this.params.chartObject = 'LINE_ITEM';
            this.chartObjects       = chartObjectsLineItem;
        } else {
            this.type   = 'flight';
            this.params.chartObject = 'FLIGHT';
            this.params.entityId    = +this.flightId;
            this.chartObjects       = chartObjectsFlight;
        }
        this.chartMetrics = expandedChartMetrics;

        return this;
    }

    private loadSeries(){
        this.wait   = true;
        this.loadFailed = false;

        this.chartService.getSeries(this.getParams()).then(seriesData  => {
            this.seriesData = seriesData;
        });
    }

    private onWindowResize(e: any){
        if (this.onResize) return;

        this.wait       = true;
        this.onResize   = true;
        setTimeout(() => {
            this.renderPlot();
            this.wait       = false;
            this.onResize   = false;
        }, 500);
    }

    public onParamsChange(e: any){
        e.stopPropagation();

        if (this.params.chartObject === 'FLIGHT' || this.params.chartObject === 'LINE_ITEM') {
            this.chartMetrics = expandedChartMetrics;
        } else {
            this.chartMetrics = chartMetrics;
            if ( chartMetrics.findIndex(f => {return f === this.params.chartMetric}) < 0 ) {
                this.params.chartMetric = 'IMPS';
            }
        }

        this.loadSeries();
    }

    private getParams(){
        let params  = Object.assign({}, this.params);
        switch (this.selectedPeriod){
            case 'LW':
                params.dateStart    = moment().subtract(1, 'week').startOf('week').format(dateFormatParse);
                params.dateEnd      = moment().subtract(1, 'week').endOf('week').format(dateFormatParse);
                break;
            case 'LM':
                params.dateStart    = moment().subtract(1, 'month').startOf('month').format(dateFormatParse);
                params.dateEnd      = moment().subtract(1, 'month').endOf('month').format(dateFormatParse);
                break;
            case 'TW':
                params.dateStart    = moment().startOf('week').format(dateFormatParse);
                params.dateEnd      = moment().endOf('week').format(dateFormatParse);
                break;
            case 'TM':
                params.dateStart    = moment().startOf('month').format(dateFormatParse);
                params.dateEnd      = moment().endOf('month').format(dateFormatParse);
                break;
            default:
                delete params.dateStart;
                delete params.dateEnd;
        }

        return params;
    }

    private renderPlot(){
        if (this.plot !== undefined) {
            this.plot.destroy();
        }

        let series      = [],
            settings    = Object.assign({}, this.getSettings());

        if (this.seriesData && this.seriesData['x'] && this.seriesData['y'] && this.seriesData['x'].length && this.seriesData['y'].length){
            this.seriesData['x'].forEach((vx,ix) => {
                let date    = new Date(+vx).toDateString();

                this.seriesData['y'].forEach((v,i) => {
                    if (series[i] === undefined) series.push([]);
                    series[i].push([date, v.points[ix]]);
                });
            });

            this.seriesData['y'].forEach((v,i) => {
                settings.series.push({
                    label: v.label,
                    color: chartColors[i]
                });
            });
            settings.axes.yaxis.label = L10nChartMetrics[this.chartMetrics.find(f => {return f === this.params.chartMetric})];
        } else {
            series  = [[[new Date().getTime(),0]]];
            settings.legend.show    = false;
        }

        if (this.params.chartType === 'TOTAL'){
            settings.seriesDefaults = {
                renderer: this.jqPlot.BarRenderer,
                pointLabels: { show: true }
            };
        }

        try {
            this.plot   = this.jqPlot(this.chartId, series, settings);
        } catch (e){
            this.loadFailed = true;
        }
    }


    private getSettings():any{
        return {
            axes:{
                xaxis:{
                    renderer: this.jqPlot.DateAxisRenderer,
                    tickOptions:{
                        formatString: "%m/%d/%y"
                    }
                },
                yaxis:{
                    label: '',
                    labelRenderer: this.jqPlot.CanvasAxisLabelRenderer,
                    min: 0
                }
            },
            highlighter: {
                tooltipContentEditor: (str, seriesIndex, pointIndex) => {
                    let date    = new Date(+this.seriesData.x[pointIndex]).toDateString(),
                        color   = chartColors[seriesIndex],
                        metrics = this.chartMetrics.find(f => {return f === this.params.chartMetric});

                    return `${date}<br><span style="color:${color}">${this.seriesData.y[seriesIndex].label}:
                        ${this.seriesData.y[seriesIndex].points[pointIndex]} ${metrics}</span>`;
                },
                show: true,
                sizeAdjust: 7.5,
                tooltipLocation: 'n'
            },
            cursor: {
                show:true,
                zoom:true,
                showTooltip:false
            },
            series:[],
            legend: {
                show: true,
                location: 'ne',
                renderer: this.jqPlot.EnhancedLegendRenderer,
                rendererOptions: {
                    numberColumns: 1
                }
            },
        };
    }
}
