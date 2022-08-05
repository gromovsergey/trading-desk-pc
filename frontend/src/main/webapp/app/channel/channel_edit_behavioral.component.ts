import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Subscription} from 'rxjs/Rx';

import {PageComponent} from '../shared/page.component';
import {AgencySessionModel} from '../agency/agency_session.model';
import {AdvertiserSessionModel} from '../advertiser/advertiser_session.model';
import {ChannelService} from './channel.service';
import {BehavioralChannel} from './behavioral_channel.model';
import {AdvertiserService} from "../advertiser/advertiser.service";
import {AgencyService} from "../agency/agency.service";
import {UserSessionModel} from "../user/user_session.model";
import {ChannelSearchModel, ChannelSearchSessionModel} from "./channel_search_session.model";

@Component({
    selector: 'ui-channel-edit-behavioral',
    templateUrl: 'edit_behavioral.html'
})

export class ChannelEditBehavioralComponent extends PageComponent implements OnInit, OnDestroy {

    private channelSearchSession: ChannelSearchSessionModel = new ChannelSearchSessionModel();
    private addInternalChannel: boolean = false;

    public backUrl: string;
    public queryBackUrl: string;
    private mode: string;

    private routerSubscription: Subscription;
    private routerQuerySubscription: Subscription;

    public title: string;
    public wait: boolean = true;
    public waitAccounts: boolean = true;
    private waitSubmit: boolean = false;
    private agencySessionModel: AgencySessionModel = new AgencySessionModel();
    private advertiserSessionModel: AdvertiserSessionModel = new AdvertiserSessionModel();
    private errors: any = {};

    private channel: BehavioralChannel;

    private accounts: Array<any>;
    private user: UserSessionModel = new UserSessionModel();

    constructor(private channelService: ChannelService,
                private advertiserService: AdvertiserService,
                private agencyService: AgencyService,
                private route: ActivatedRoute,
                private router: Router) {
        super();
        if (this.mode) {
            this.initTitle();
        }
    }

    ngOnInit() {
        this.routerQuerySubscription = this.route.queryParams.subscribe(params => {
            this.queryBackUrl = params && params.backUrl ? params.backUrl : null;
        });

        this.routerSubscription = this.route.url.subscribe(params => {
            let path = params[1].path;
            if (path === 'add') {
                this.mode = 'add';
                this.initTitle();
                this.channel = new BehavioralChannel();
                this.initAccount(params[2] ? +params[2].path : null);
                this.wait = false;
            } else {
                this.mode = 'edit';
                this.initTitle();
                this.channelService.getBehavioralById(+path)
                    .then(foundChannel => {
                        this.channel = foundChannel;
                        this.initAccount(this.channel.account.id);
                        this.wait = false;
                    });
            }
        });
    }

    private initAccount(accountId: number) {
        if (!accountId) {
            this.addInternalChannel = true;
            this.channelService.getInternalAccounts()
                .then(accounts => {
                    this.accounts = accounts;
                    this.channel.account.id = this.user.accountId;
                    this.channel.country = 'RU';
                    this.backUrl = `/channel/search`;
                    this.waitAccounts = false;
                });
        } else {
            this.channelService.getAccountById(accountId)
                .then(account => {
                    if (this.mode === 'add') {
                        this.channel.account.id = +accountId;
                        this.channel.country = account.countryCode;
                    }

                    if (account.role === 'ADVERTISER') {
                        this.advertiserService.getById(accountId)
                            .then(advertiserAccount => {
                                this.advertiserSessionModel.data = advertiserAccount;
                                this.agencySessionModel.clear();
                                this.backUrl = `/advertiser/${this.advertiserSessionModel.id}/channels`;
                            });
                    } else if (account.role === 'AGENCY') {
                        this.agencyService.getById(accountId)
                            .then(agencyAccount => {
                                this.agencySessionModel.data = agencyAccount;
                                this.advertiserSessionModel.clear();
                                this.backUrl = `/agency/${this.agencySessionModel.id}/channels`;
                            });
                    } else {
                        this.backUrl = `/channel/search`;
                    }

                    this.waitAccounts = false;
                });
        }
    }

    private initTitle() {
        if (this.mode === 'add') {
            this.title = '_L10N_(button.add)' + ' ' + '_L10N_(channel.blockName.behavioural)';
        } else {
            this.title = '_L10N_(button.edit)' + ' ' + '_L10N_(channel.blockName.behavioural)';
        }
    }

    ngOnDestroy() {
        if (this.routerSubscription) {
            this.routerSubscription.unsubscribe();
        }
        if (this.routerQuerySubscription) {
            this.routerQuerySubscription.unsubscribe();
        }
    }

    public submitForm() {
        this.waitSubmit = true;

        let promise;
        if (this.mode === 'add') {
            if (this.addInternalChannel) {
                this.channel.visibility = 'PUB';
            }
            promise = this.channelService.createBehavioral(this.channel);
        } else {
            promise = this.channelService.updateBehavioral(this.channel);
        }

        promise
            .then(id => {
                if (this.addInternalChannel) {
                    let channelSearchModel = new ChannelSearchModel();
                    channelSearchModel.name = this.channel.name;
                    channelSearchModel.accountId = this.channel.account.id.toString();
                    this.channelSearchSession.setData(channelSearchModel);
                }
                this.waitSubmit = false;
                this.router.navigateByUrl(this.getBackUrl());
            })
            .catch(e => {
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

    public getBackUrl(): string {
        return this.queryBackUrl ? this.queryBackUrl : this.backUrl;
    }
}
