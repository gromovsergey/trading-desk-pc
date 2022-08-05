import {Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';

import {moment, dateFormatShort} from '../common/common.const';
import {PageComponent} from '../shared/page.component';
import {Subscription} from "rxjs/Subscription";
import {AudienceResearch, AudienceResearchChannel, Channel, Account} from "./audienceresearch.model";
import {ActivatedRoute, Router} from "@angular/router";
import {AudienceResearchService} from "./audienceresearch.service";
import {L10nAudienceResearchChartTypes} from '../common/L10n.const';

@Component({
    selector: 'ui-audience-research-edit',
    templateUrl: 'edit.html'
})

export class AudienceResearchEditComponent extends PageComponent implements OnInit, OnDestroy {

    public mode: string;
    private routerSubscription: Subscription;

    public title: string;
    public backUrl: string = '/audienceresearch/list';
    public audienceResearch: AudienceResearch;
    public initialChannels: Array<AudienceResearchChannel> = [];

    public wait: boolean = true;
    public waitSubmit: boolean = false;
    private errors: any = {};

    @ViewChild('textInput1') textInputEl1: ElementRef;
    public waitTargetSearch: boolean;
    public autocompleteTargetChannels: Array<any>;

    @ViewChild('textInput2') textInputEl2: ElementRef;
    public waitSearch: boolean;
    public autocompleteChannels: Array<Channel>;
    public currentChannelIds: Array<number> = [];

    @ViewChild('textInput3') textInputEl3: ElementRef;
    public accounts: Array<Account>;
    public showAccounts: boolean;

    private inputTimer;
    private keypDelay: number = 300;

    public moment = moment;
    public dateFormatShort = dateFormatShort;

    public L10nAudienceResearchChartTypes = L10nAudienceResearchChartTypes;
    public chartTypes: Array<string> = ['BAR_VERTICAL', 'BAR_HORIZONTAL', 'DONUT', 'GOOGLE_GEOCHART'];

    public showCharts: boolean = false;
    public currentChannel: AudienceResearchChannel;
    public chartsPopupOptions;

    constructor(private audienceResearchService: AudienceResearchService,
                private route: ActivatedRoute,
                private router: Router) {
        super();
        if (this.mode) {
            this.initTitle();
        }
     }

    ngOnInit() {
        this.routerSubscription = this.route.url.subscribe(params => {
            let path = params[0].path;
            if (path === 'add') {
                this.mode = 'add';
                this.audienceResearch = new AudienceResearch();
                this.initTitle();

                this.audienceResearchService.getAdvertisers().then(res => {
                    this.accounts = res;
                    this.wait = false;
                });

            } else {
                this.mode = 'edit';

                Promise.all([
                    this.audienceResearchService.getById(+path),
                    this.audienceResearchService.getAdvertisers()
                ]).then(res => {
                    this.audienceResearch = res[0];
                    this.initialChannels = this.audienceResearch.channels;
                    this.currentChannelIds = this.audienceResearch.channels.map(v => v.channel.id);
                    this.initTitle();

                    this.accounts = res[1];

                    this.chartsPopupOptions = {
                        title: '_L10N_(audienceResearch.researchForChannel) ' + this.audienceResearch.targetChannel.name,
                        btnTitle: '',
                        btnIcon: null,
                        btnIconDisabled: false,
                        size: 'lg'
                    };

                    this.wait = false;
                });
            }
        });
    }

    private initTitle() {
        if (this.mode === 'add') {
            this.title = '_L10N_(audienceResearch.add)';
        } else {
            this.title = '_L10N_(audienceResearch.edit)' + ': ' + this.audienceResearch.targetChannel.name;
        }
    }

    ngOnDestroy() {
        if (this.routerSubscription) {
            this.routerSubscription.unsubscribe();
        }
    }

    public hideTargetAutocomplete(e?: any) {
        this.autocompleteTargetChannels = null;
    }

    public clearTargetAutocomplete(e?: any) {
        this.hideTargetAutocomplete();
        this.textInputEl1.nativeElement.value = '';
        this.textInputEl1.nativeElement.focus();
    }

    public textTargetInputChange(e: any) {
        if (this.waitTargetSearch) return;
        if (this.inputTimer) clearTimeout(this.inputTimer);

        this.inputTimer = setTimeout(() => {
            let text = this.textInputEl1.nativeElement.value;

            this.hideTargetAutocomplete();

            if (text.length) {
                this.waitTargetSearch = true;

                this.audienceResearchService.getChannels(text, null, false)
                    .then(res => {
                        this.autocompleteTargetChannels = res;
                        this.waitTargetSearch = false;

                        setImmediate(() => {
                            this.textInputEl1.nativeElement.focus();
                        });
                    });
            }
        }, this.keypDelay);
    }

    public selectTargetChannel(e: any, channel: any){
        e.preventDefault();

        this.audienceResearch.targetChannel.id = channel.id;
        this.audienceResearch.targetChannel.name = channel.name;

        this.textInputEl1.nativeElement.value = channel.name;
        this.textInputEl1.nativeElement.focus();
        this.hideTargetAutocomplete();
    }

    public hideAutocomplete(e?: any) {
        this.autocompleteChannels = null;
    }

    public clearAutocomplete(e?: any) {
        this.hideAutocomplete();
        this.textInputEl2.nativeElement.value = '';
        this.textInputEl2.nativeElement.focus();
    }

    public textInputChange(e: any) {
        if (this.waitSearch) return;
        if (this.inputTimer) clearTimeout(this.inputTimer);

        this.inputTimer = setTimeout(() => {
            let text = this.textInputEl2.nativeElement.value;

            this.hideAutocomplete();

            if (text.length) {
                this.waitSearch = true;

                this.audienceResearchService.getChannels(text, 'E', true)
                    .then(res => {
                        this.autocompleteChannels = res.filter(v => {
                            return !this.currentChannelIds.includes(v.id);
                        });
                        this.waitSearch = false;

                        setImmediate(() => {
                            this.textInputEl2.nativeElement.focus();
                        });
                    });
            }
        }, this.keypDelay);
    }

    public addChannel(e: any, channel: Channel){
        e.preventDefault();

        let sortOrder = 1;
        if (this.audienceResearch.channels.length > 0) {
            sortOrder = this.audienceResearch.channels[this.audienceResearch.channels.length - 1].sortOrder + 1;
        }

        let audienceResearchChannel = this.initialChannels.filter(v => {
            return v.channel.id === channel.id;
        })[0];

        if (audienceResearchChannel) {
            audienceResearchChannel.sortOrder = sortOrder;
            this.audienceResearch.channels.push(audienceResearchChannel);
        } else {
            audienceResearchChannel = new AudienceResearchChannel();
            audienceResearchChannel.channel = Object.assign({}, channel);
            audienceResearchChannel.sortOrder = sortOrder;
            this.audienceResearch.channels.push(audienceResearchChannel);
        }

        this.currentChannelIds.push(channel.id);

        this.textInputEl2.nativeElement.value = '';
        this.textInputEl2.nativeElement.focus();
        this.hideAutocomplete();
    }

    private deleteChannel(e: any, id: number) {
        this.audienceResearch.channels = this.audienceResearch.channels.filter(v => {
            return v.channel.id !== id;
        });
        this.currentChannelIds = this.currentChannelIds.filter(v => {
            return v != id;
        });
    }

    public showAdvertiserSelect(e: any) {
        e.preventDefault();
        if (!this.showAccounts) {
            this.showAccounts = true;
        } else {
            this.showAccounts = false;
        }
    }

    public addAdvertiser(e: any, account: Account) {
        e.preventDefault();
        if (this.audienceResearch.advertisers.find(c => c.id === account.id) === undefined) {
            this.audienceResearch.advertisers.push(Object.assign({}, account));
        }
        this.textInputEl3.nativeElement.value = '';
        this.textInputEl3.nativeElement.focus();
        this.showAccounts = false;
    }

    public deleteAdvertiser(e: any, id: number) {
        this.audienceResearch.advertisers = this.audienceResearch.advertisers.filter(v => {
            return v.id !== id;
        });
    }

    public changeOrders(e: any, idx1: number, idx2: number) {
        e.preventDefault();

        let tmp = this.audienceResearch.channels[idx1];
        this.audienceResearch.channels[idx1] = this.audienceResearch.channels[idx2];
        this.audienceResearch.channels[idx2] = tmp;

        let tmpOrder = this.audienceResearch.channels[idx1].sortOrder;
        this.audienceResearch.channels[idx1].sortOrder = this.audienceResearch.channels[idx2].sortOrder;
        this.audienceResearch.channels[idx2].sortOrder = tmpOrder;
    }

    private submitForm() {
        this.waitSubmit = true;

        this.prepareSortOrders();

        let promise;
        if (this.mode === 'add') {
            promise = this.audienceResearchService.create(this.audienceResearch);
        } else {
            promise = this.audienceResearchService.update(this.audienceResearch);
        }

        promise.then(id => {
            this.waitSubmit = false;
            this.router.navigateByUrl(this.backUrl);
        }).catch(e => {
            if (e.status === 412) {
                this.errors = e.json();
                this.waitSubmit = false;

                setImmediate(() => {
                    let errorEl = window.document.querySelector('.has-error');
                    if (errorEl !== null) {
                        let bounds = errorEl.getBoundingClientRect();
                        window.scrollTo(0, window.scrollY + bounds.top - 70);
                    }
                });
            }
        });
    }

    private prepareSortOrders() {
        let i = 1;
        this.audienceResearch.channels.sort(
            function (a: AudienceResearchChannel, b: AudienceResearchChannel) {
                return a.sortOrder === b.sortOrder ? 0 : a.sortOrder > b.sortOrder ? 1 : -1;
            }
        ).forEach(c => {
            c.sortOrder = i++;
        });
    }

    private hideChartsPopup(e:any) {
        this.currentChannel = undefined;
        this.showCharts = false;
    }

    public showChartsPopup(e: any, currentChannel: AudienceResearchChannel) {
        e.preventDefault();
        this.currentChannel = currentChannel;
        this.showCharts = true;
    }

    public updateYesterdayComment(e: any, id: number) {
        this.audienceResearchService.updateYesterdayComment(e.comment, id);
    }

    public updateTotalComment(e: any, id: number) {
        this.audienceResearchService.updateTotalComment(e.comment, id);
    }
}
