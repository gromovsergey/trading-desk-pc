import {Component, OnDestroy, OnInit, AfterViewInit, ViewChildren, QueryList} from '@angular/core';

import {PageComponent} from '../shared/page.component';
import {Subscription} from "rxjs/Subscription";
import {AudienceResearch, AudienceResearchChannel} from "./audienceresearch.model";
import {ActivatedRoute} from "@angular/router";
import {AudienceResearchService} from "./audienceresearch.service";
import {AudienceResearchChartComponent} from "./audienceresearch-chart.component";
import {moment} from '../common/common.const';
import * as util from '../common/utilities';

@Component({
    selector: 'ui-audience-research-view',
    templateUrl: 'view.html'
})

export class AudienceResearchViewComponent extends PageComponent implements OnInit, AfterViewInit, OnDestroy {

    private routerSubscription: Subscription;

    public title: string;
    public backUrl: string = '/audienceresearch/list';
    public audienceResearch: AudienceResearch;
    private channelStack: Array<AudienceResearchChannel>;
    public channels: Array<AudienceResearchChannel> = [];
    @ViewChildren(AudienceResearchChartComponent) charts: QueryList<AudienceResearchChartComponent>;

    public canUpdateResearch: boolean;

    public wait: boolean = true;

    constructor(private audienceResearchService: AudienceResearchService,
                private route: ActivatedRoute) {
        super();
    }

    ngOnInit() {
        this.routerSubscription = this.route.params.subscribe(params => {
            Promise.all([
                this.audienceResearchService.getById(params['id']),
                this.audienceResearchService.isAllowedLocal0('audienceResearch.edit')
            ]).then(res => {
                this.audienceResearch = res[0];
                this.audienceResearch.channels = res[0].channels.filter(c => {
                    return moment().startOf('day').diff(moment(c.startDate).startOf('day'), 'days') > 0
                });
                this.channelStack = [...this.audienceResearch.channels];
                this.channels.push(this.channelStack.shift());
                this.audienceResearchService.getDynamicLocalizations(this.audienceResearch.targetChannel.id).then(localizations => {
                    let ruLocalizations = localizations.filter(c => c.lang == 'ru');
                    if (ruLocalizations.length > 0) {
                        this.initTitle(ruLocalizations[0].value);
                    } else {
                        this.initTitle(this.audienceResearch.targetChannel.name);
                    }
                    this.canUpdateResearch = res[1];
                    this.wait = false;
                })
            });
        });
    }

    ngAfterViewInit() {
      util.onWindowEvent('scroll.audienceResearch', () => {
        let element = document.documentElement;
        let isVisible = element.scrollHeight - element.scrollTop === element.clientHeight;
        isVisible && this.nextPage();
      })
    }

    public nextPage(): void {
      this.charts.last.wait1 || this.charts.last.wait2 || this.channelStack.length && this.channels.push(this.channelStack.shift());
    }

    private initTitle(localizedChannelName: string) {
        this.title = '_L10N_(audienceResearch.researchForChannel) ' + localizedChannelName;
    }

    ngOnDestroy() {
        if (this.routerSubscription) {
            this.routerSubscription.unsubscribe();
        }
        util.offWindowEvent('.audienceResearch');
    }

    public updateYesterdayComment(e: any, id: number) {
        this.audienceResearchService.updateYesterdayComment(e.comment, id);
    }

    public updateTotalComment(e: any, id: number) {
        this.audienceResearchService.updateTotalComment(e.comment, id);
    }
}
