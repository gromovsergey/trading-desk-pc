import {Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Subscription} from 'rxjs/Rx';

import {PageComponent} from '../shared/page.component';
import {AgencySessionModel} from '../agency/agency_session.model';
import {AdvertiserSessionModel} from '../advertiser/advertiser_session.model';
import {ChannelService} from './channel.service';
import {ExpressionChannel} from "./expression_channel.model";
import {UserSessionModel} from "../user/user_session.model";
import {AdvertiserService} from "../advertiser/advertiser.service";
import {AgencyService} from "../agency/agency.service";
import {ChannelSearchModel, ChannelSearchSessionModel} from "./channel_search_session.model";
import {IdName} from "../shared/idname.model";

@Component({
    selector: 'ui-channel-edit-expression',
    templateUrl: 'edit_expression.html'
})

export class ChannelEditExpressionComponent extends PageComponent implements OnInit, OnDestroy {

    @ViewChild('textInput') textInputEl: ElementRef;

    private externalChannelSources = process.env._EXTERNAL_CHANNEL_SOURCES_;
    private ownChannelSource = process.env._OWN_CHANNEL_SOURCE_;

    private channelSearchSession: ChannelSearchSessionModel = new ChannelSearchSessionModel();
    private internalChannelFlag: boolean = false;

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

    private channel: ExpressionChannel;

    public accountId: number;
    private accounts: Array<any>;
    private user: UserSessionModel = new UserSessionModel();

    public popupOptions;
    public popupVisible: boolean = false;
    public popupWait: boolean = false;
    public autocomplete: Array<any>;
    private inputTimer;
    public channelsLink: Array<any>;
    private currentIdx: number;
    private isCurrentExcluded: boolean = false;

    public showChannelTreeFlag: boolean = false;

    constructor(private channelService: ChannelService,
                private advertiserService: AdvertiserService,
                private agencyService: AgencyService,
                private route: ActivatedRoute,
                private router: Router) {
        super();

        this.popupOptions = {
            title: '_L10N_(channel.expression.linkChannels)',
            hint: '_L10N_(channel.expression.linkChannels.hint)',
            btnTitle: '_L10N_(button.save)',
            btnIconDisabled: false
        };

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
                this.channel = new ExpressionChannel();
                this.initAccount(params[2] ? +params[2].path : null);
                this.wait = false;
            } else {
                this.mode = 'edit';
                this.initTitle();
                this.channelService.getExpressionById(+path)
                    .then(foundChannel => {
                        this.channel = foundChannel;
                        this.initAccount(this.channel.accountId);
                        this.wait = false;
                    });
            }
        });
    }

    private initAccount(accountId: number) {
        if (!accountId) {
            this.internalChannelFlag = true;
            this.channelService.getInternalAccounts()
                .then(accounts => {
                    this.accounts = accounts;
                    this.channel.accountId = this.user.accountId;
                    this.channel.country = 'RU';
                    this.backUrl = `/channel/search`;
                    this.waitAccounts = false;
                });
        } else {
            this.accountId = +accountId;
            this.channelService.getAccountById(this.accountId)
                .then(account => {
                    if (this.mode === 'add') {
                        this.channel.accountId = this.accountId;
                        this.channel.country = account.countryCode;
                    }

                    if (account.role === 'ADVERTISER') {
                        this.advertiserService.getById(this.accountId)
                            .then(advertiserAccount => {
                                this.advertiserSessionModel.data = advertiserAccount;
                                this.agencySessionModel.clear();
                                this.backUrl = `/advertiser/${this.advertiserSessionModel.id}/channels`;
                            });
                    } else if (account.role === 'AGENCY') {
                        this.agencyService.getById(this.accountId)
                            .then(agencyAccount => {
                                this.agencySessionModel.data = agencyAccount;
                                this.advertiserSessionModel.clear();
                                this.backUrl = `/agency/${this.agencySessionModel.id}/channels`;
                            });
                    } else {
                        this.internalChannelFlag = true;
                        this.backUrl = `/channel/search`;
                    }

                    this.waitAccounts = false;
                });
        }
    }

    private initTitle() {
        if (this.mode === 'add') {
            this.title = '_L10N_(button.add)' + ' ' + '_L10N_(channel.blockName.expression)';
        } else {
            this.title = '_L10N_(button.edit)' + ' ' + '_L10N_(channel.blockName.expression)';
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
            if (this.internalChannelFlag) {
                this.channel.visibility = 'PUB';
            }
            promise = this.channelService.createExpression(this.channel);
        } else {
            promise = this.channelService.updateExpression(this.channel);
        }

        promise
            .then(id => {
                if (this.internalChannelFlag) {
                    let channelSearchModel = new ChannelSearchModel();
                    channelSearchModel.name = this.channel.name;
                    channelSearchModel.accountId = this.channel.accountId.toString();
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

    public getChannelName(channel: any) {
        return this.internalChannelFlag ? channel.name :
            channel.localizedName;
    }

    private show(e?: any): void {
        if (this.internalChannelFlag) {
            this.showPopup(e);
        } else {
            this.showChannelTree(e);
        }
    }

    public addAudience(e?: any) {
        this.currentIdx = this.channel.includedChannels.length;
        this.isCurrentExcluded = false;
        this.channelsLink = [];
        this.show(e);
    }

    public editAudience(e: any, idx: number) {
        this.currentIdx = idx;
        this.isCurrentExcluded = false;
        this.channelsLink = this.channel.includedChannels[idx].slice();
        this.show(e);
    }

    public deleteAudience(e: any, idx: number) {
        this.channel.includedChannels.splice(idx, 1);
    }

    public addExcludedAudience(e?: any) {
        this.currentIdx = this.channel.excludedChannels.length;
        this.isCurrentExcluded = true;
        this.channelsLink = [];
        this.show(e);
    }

    public editExcludedAudience(e: any, idx: number) {
        this.currentIdx = idx;
        this.isCurrentExcluded = true;
        this.channelsLink = this.channel.excludedChannels[idx].slice();
        this.show(e);
    }

    public deleteExcludedAudience(e: any, idx: number) {
        this.channel.excludedChannels.splice(idx, 1);
    }

    private showPopup(e?: any) {
        if (e) {
          e.preventDefault();
          e.stopPropagation();
        }

        this.hideAutocomplete();
        this.popupVisible   = true;

        setImmediate(() => {
            this.textInputEl.nativeElement.focus();
        });
    }

    public hideAutocomplete(e?: any) {
        this.autocomplete = null;
    }

    public textInputChange(e: any) {
        if (this.popupWait) return;

        let textarea = this.textInputEl.nativeElement;

        if (this.inputTimer) clearTimeout(this.inputTimer);

        this.inputTimer = setTimeout(() => {
            let text = textarea.value;

            this.hideAutocomplete();

            if (text.length >= 3) {
                this.popupWait = true;

                this.channelService
                    .getExpressionChannels(this.channel.accountId, this.channel.country, text)
                    .then(list => {
                        this.autocomplete = this.removeDuplicates(list);
                        this.popupWait = false;

                        setImmediate(() => {
                            this.textInputEl.nativeElement.focus();
                        });
                    });

            }
        }, 500);
    }

    private removeDuplicates(list: Array<any>): Array<any> {
        if (list.length && this.channelsLink.length) {
            return list.filter(v => {
                return !this.channelsLink.find(f => {
                    return f.id === v.id;
                });
            });
        } else {
            return list;
        }
    }

    private addChannel(e: any, channel: any){
        e.preventDefault();

        this.channelsLink.push(channel);

        this.clearTextarea();
        this.hideAutocomplete();
    }

    private clearTextarea(){
        this.textInputEl.nativeElement.value    = '';
        this.textInputEl.nativeElement.focus();
    }

    private removeChannel(e: any, id: number){
        e.preventDefault();

        this.channelsLink   = this.channelsLink.filter(v => {
            return v.id !== id;
        })
    }

    public popupSave(e?: any) {
        this.popupWait = true;
        this.processLinkedChannels();
        this.popupHide();
    }

    private processLinkedChannels(): void {
        if (!this.isCurrentExcluded) {
            if (this.channelsLink.length == 0) {
                this.channel.includedChannels.splice(this.currentIdx, 1);
            } else {
                this.channel.includedChannels[this.currentIdx] = this.channelsLink;
            }
        } else {
            if (this.channelsLink.length == 0) {
                this.channel.excludedChannels.splice(this.currentIdx, 1);
            } else {
                this.channel.excludedChannels[this.currentIdx] = this.channelsLink;
            }
        }
    }

    public popupHide(e?: any) {
        this.hideAutocomplete();
        this.popupVisible = false;
        this.popupWait = false;

        this.clearTextarea();
        this.channelsLink = [];
    }

    public showChannelTree(e: any): void {
        if (e) {
            e.preventDefault();
            e.stopPropagation();
        }

        this.showChannelTreeFlag = true;
    }

    public onChannelTreeClose(): void {
        this.showChannelTreeFlag = false;
        this.channelsLink = [];
    }

    public onChannelTreeSave(selectedChannels: Array<IdName>): void {
        this.wait = true;
        this.channelService.getExternalChannels(this.accountId, selectedChannels.map(c => c.id))
            .then( channels => {
                this.channelsLink = channels;
                this.processLinkedChannels();
                this.onChannelTreeClose();
                this.wait = false;
            });
    }

    public getSelectedChannelIdNames(): Array<IdName> {
        return this.channelsLink.map( c => new IdName(c.id, c.name) );
    }

    public getChannelSources(): Array<string> {
        let result = this.externalChannelSources
            .split(',')
            .map( s => s.trim() );
        result.push(this.ownChannelSource.trim());
        return result;
    }

    public getBackUrl(): string {
        return this.queryBackUrl ? this.queryBackUrl : this.backUrl;
    }
}
