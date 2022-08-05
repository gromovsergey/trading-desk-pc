import {Component, EventEmitter, Input, OnChanges, OnDestroy, Output} from "@angular/core";
import {jQuery as $, guid} from "../common/common.const";
import ChartDefaultConfig from '../common/chart-utilities';
import {AudienceResearchService} from "./audienceresearch.service";
import {AudienceResearchStat} from "./audienceresearchstat.model";

@Component({
    selector: 'ui-audience-research-chart',
    templateUrl: 'chart.html'
})
export class AudienceResearchChartComponent implements OnChanges, OnDestroy {
    @Input() template: string;
    @Input() audienceResearchId: number;
    @Input() channelId: number;
    @Input() canEdit: boolean = true;
    @Input() yesterdayComment: string;
    @Input() totalComment: string;
    @Input() inOneRow: boolean;

    @Output() updateYesterdayComment: EventEmitter<any> = new EventEmitter(true);
    @Output() updateTotalComment: EventEmitter<any> = new EventEmitter(true);

    public title: string;
    public noData: boolean;
    public actionErrors: boolean;

    public containerId1 = guid();
    public chart1: any;
    public wait1 = true;
    public error1: any;

    public containerId2 = guid();
    public chart2: any;
    public wait2 = true;
    public error2: any;

    constructor(private audienceResearchService: AudienceResearchService) {
    }

    ngOnChanges() {
        this.loadStat();
    }

    ngOnDestroy() {
        if (this.chart1) {
            this.chart1.destroy();
        }
        if (this.chart2) {
            this.chart2.destroy();
        }
    }

    public loadStat() {
        this.actionErrors = undefined;
        this.error1 = undefined;
        this.error2 = undefined;
        this.wait1 = true;
        this.wait2 = true;
        this.noData = false;

        this.audienceResearchService.getStat(this.audienceResearchId, this.channelId)
            .then((data) => {
                this.title = data.channelName;

                if (data.values.length == 0) {
                    this.noData = true;
                    this.wait1 = false;
                    this.wait2 = false;
                    return;
                }

                // REMOVE!!
                if(this.title == 'Разбивка по геоположению пользователей (города-милионники)') {
                    this.template = 'GOOGLE_GEOCHART';
                }

                //chart 1
                this.chart1 = new ChartDefaultConfig[this.template]._chart(this.containerId1);
                this.renderChart(this.chart1, this.template, data, this.yesterdayComment)
                    .then((result) => {
                        this.wait1 = false;
                    })
                    .catch((e) => {
                        this.error1 = e;
                        this.wait1 = false;
                    });

                //chart 2
                this.chart2 = new ChartDefaultConfig['LINE_TIME']._chart(this.containerId2);
                this.renderChart(this.chart2, 'LINE_TIME', data, this.totalComment)
                    .then((result) => {
                        this.wait2 = false;
                    })
                    .catch((e) => {
                        this.error2 = e;
                        this.wait2 = false;
                    });
            })
            .catch(e => {
                this.actionErrors = e.json()['actionError'];
                this.wait1 = false;
                this.wait2 = false;
            });
    }

    private renderChart(chart: any, template: string, data: AudienceResearchStat, comments: string) {
        let title = template == 'LINE_TIME' ? '_L10N_(audienceResearch.titleAllTime)' : '_L10N_(audienceResearch.titleForDate) ' + data.lastDate;
        let config = this.createConfig(template, title, data, comments);
        return chart.renderChart(this.prepareData(template, data), config);
    }

    private createConfig(template: string, title: string, data: AudienceResearchStat, comments: string) {
        switch (template) {
            case 'BAR_VERTICAL':
                return {
                    template: template,
                    comments: comments,
                    options: {
                        title: title,
                        axes: {
                            xaxis: {
                                ticks: data.ticks
                            }
                        }
                    }
                };
            case 'BAR_HORIZONTAL':
                return {
                    template: template,
                    comments: comments,
                    options: {
                        title: title,
                        seriesDefaults: {
                            rendererOptions: {
                                barMargin: 2
                            }
                        },
                        axes: {
                            yaxis: {
                                ticks: data.ticks
                            }
                        }
                    }
                };
            case 'DONUT':
                return {
                    template: template,
                    comments: comments,
                    options: {
                        title: title,
                        legend: {
                            labels: data.ticks
                        }
                    }
                };
            case 'GOOGLE_GEOCHART':
                let preparedData = this.prepareData(template, data);
                let selected = preparedData
                    .sort((a, b) => b[1] - a[1])
                    .map(v => v[0])
                    .slice(0, 5);

                return {
                    template: template,
                    comments: comments,
                    options: {
                        _title: title,
                        _head: ['Город', 'Индекс'],
                        _selected: selected,
                        region: 'RU',
                        resolution: 'provinces',
                        markerOpacity: 0.8,
                        colorAxis: {colors: ['yellow', 'orange']},
                        defaultColor: 'white',
                        sizeAxis: {
                            minSize: 3,
                            maxSize: 10
                        },
                        legend: {textStyle: {color: '#666', fontSize: 14}}
                    }
                };
            case 'LINE_TIME':
                return {
                    template: template,
                    comments: comments,
                    options: {
                        title: title,
                        legend: {
                            labels: data.ticks
                        },
                        axes: {
                            xaxis: {
                                ticks: data.dates
                            }
                        }
                    }
                };
            default:
                return undefined;
        }
    }

    private prepareData(template: string, data: AudienceResearchStat) {
        switch (template) {
            case 'BAR_VERTICAL':
            case 'BAR_HORIZONTAL':
            case 'DONUT':
                return [data.lastValues.map(val => Math.round(val))];
            case 'GOOGLE_GEOCHART':
                let preparedData = [];
                data.ticks.forEach((item, index) => {
                    preparedData[index] = [item, Math.round(data.lastValues[index])];
                });
                return preparedData;
            case 'LINE_TIME':
                let convertedData = [];
                data.values.forEach((item, index) => {
                    item.forEach((item2, index2) => {
                        if (index == 0) {
                            convertedData[index2] = [data.values.length];
                        }
                        convertedData[index2][index] = item2;
                    });
                });
                return convertedData;
            default:
                return undefined;
        }
    }

    public toggleYesterdayComment(event: any): void {
        $(`#${this.containerId1}`).parent()
            .find('div.cinemagoers-chart-comments, .cinemagoers-chart-comments-control').toggle()
            .find('textarea').prop('readonly', true);
        $(event.target).toggleClass('active');
    }

    public toggleTotalComment(event: any): void {
        $(`#${this.containerId2}`).parent()
            .find('div.cinemagoers-chart-comments, .cinemagoers-chart-comments-control').toggle()
            .find('textarea').prop('readonly', true);
        $(event.target).toggleClass('active');
    }

    public editYesterdayComment(event: any): void {
        $(`#${this.containerId1}`).parent()
            .find('div.cinemagoers-chart-comments textarea')
            .prop('readonly', function (idx, val) {
                return !val;
            });
    }

    public editTotalComment(event: any): void {
        $(`#${this.containerId2}`).parent()
            .find('div.cinemagoers-chart-comments textarea')
            .prop('readonly', function (idx, val) {
                return !val;
            });
    }

    public onYesterdayCommentChange(event: any) {
        this.updateYesterdayComment.emit({
            comment: this.yesterdayComment
        });
        $(`#${this.containerId1}`).parent()
            .find('div.cinemagoers-chart-comments textarea')
            .prop('readonly', true);
    }

    public onTotalCommentChange(event: any) {
        this.updateTotalComment.emit({
            comment: this.totalComment
        });
        $(`#${this.containerId2}`).parent()
            .find('div.cinemagoers-chart-comments textarea')
            .prop('readonly', true);
    }
}
