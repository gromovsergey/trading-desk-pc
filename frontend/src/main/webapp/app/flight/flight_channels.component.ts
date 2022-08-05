import {Component, OnChanges, Input, Output, ViewChild, ElementRef, EventEmitter, ContentChild} from '@angular/core';

import { LoadingComponent }                                from '../shared/loading.component';
import { IconComponent }                                   from '../shared/icon.component';
import { DropdownButtonComponent, DropdownButtonMenuItem } from '../shared/dropdown_button.component';
import { DisplayStatusToggleComponent }                    from '../shared/display_status_toggle.component';
import { PopupComponent }                                  from '../shared/popup.component';
import { DisplayStatusDirective }                          from '../shared/display_status.directive';
import { moment, dateFormatShort }                         from '../common/common.const';
import { L10nCountries, L10nChannelTypes }                 from '../common/L10n.const';
import { LineItemService }                                 from '../lineitem/lineitem.service';
import { AgencySessionModel }                              from '../agency/agency_session.model';
import { AdvertiserSessionModel }                          from '../advertiser/advertiser_session.model';
import { ChannelService }                                  from '../channel/channel.service';
import { FlightService }                                   from './flight.service';
import {IdName} from "../shared/idname.model";
import {UserSessionModel} from "../user/user_session.model";


@Component({
    selector: 'ui-flight-channels',
    templateUrl: 'channels.html'
})

export class FlightChannelsComponent implements OnChanges {

    @Input() flightId: number;
    @Input() lineItemId: number;
    @Input() linkSpecialChannelFlag: boolean;
    @Input() specialChannelId: number;
    @Input() short: boolean;
    @Input() readonly: boolean = false;

    @Output() onStatusChange    = new EventEmitter();

    @ViewChild('textInput') textInputEl: ElementRef;
    @ViewChild('checkAll') checkAllInput: ElementRef;
    @ContentChild('channelsNotifications') channelsNotificationsEl: ElementRef;
    @ViewChild('channelsNotifications') channelsNotificationsChildEl: ElementRef;

    private externalChannelSources = process.env._EXTERNAL_CHANNEL_SOURCES_;
    private ownChannelSource = process.env._OWN_CHANNEL_SOURCE_;

    public wait: boolean   = true;
    public channels: Array<any>;
    private bulkMenu    = [];
    public popupVisible: boolean   = false;
    public popupOptions;
    public popupWait: boolean  = false;
    public canSearchChannelsByName = false;
    public canLocalize = false;
    public canUpdateChannels = false;
    private inputTimer;
    public accountId: number;
    public autocomplete: Array<any>;
    public channelsLink: Array<any>    = [];
    public expressionError: Array<string>;

    public showStats: string;
    public waitStats: boolean  = false;
    private behavioralStats: any;
    private expressionStats: any;
    private moment  = moment;
    private dateFormatShort = dateFormatShort;
    public statsPopup;

    private service;
    public entityId: number;

    public L10nCountries = L10nCountries;
    public L10nChannelTypes = L10nChannelTypes;

    public showChannelTreeFlag: boolean = false;
    public doLinkSpecialChannel: boolean;
    public doLinkSpecialChannelTreeFlag: boolean;
    public dynamicLocalizationsChannelId: number = null;

    constructor(
        private flightService: FlightService,
        private lineItemService: LineItemService,
        private channelService: ChannelService){

        this.popupOptions = {
            title:      '_L10N_(flight.button.linkChannels)',
            btnTitle:   '_L10N_(button.link)',
            btnIcon:    'link',
            btnIconDisabled:    false
        };
        this.statsPopup = {
            title:    '_L10N_(channel.blockName.channel.statistics)',
            btnTitle: '',
            btnIcon:  null,
            btnIconDisabled: false,
            size: 'lg'
        };

        let agency      = new AgencySessionModel(),
            advertiser  = new AdvertiserSessionModel();

        this.accountId  = agency.hasData() ? agency.id : advertiser.id;
        this.bulkMenu.push(
            new DropdownButtonMenuItem('_L10N_(button.unlink)', {onclick: this.deleteChannels.bind(this)})
        );
    }

    ngOnChanges(){
        if (this.channelsNotificationsChildEl && this.channelsNotificationsEl) {
            this.channelsNotificationsChildEl.nativeElement.appendChild(this.channelsNotificationsEl.nativeElement);
        }

        this.service    = this.flightId ? this.flightService : this.lineItemService;
        this.entityId   = this.flightId ? this.flightId : this.lineItemId;
        this.doLinkSpecialChannel = this.linkSpecialChannelFlag;

        this.loadChannelsList();
    }

    private loadChannelsList() {
        let isInternal: boolean = new UserSessionModel().isInternal();
        this.wait = true;

        Promise.all([
            this.service.isAllowedLocal0('channel.search'),
            isInternal ? this.service.isAllowedLocal0('localization.update') : Promise.resolve(false),
            isInternal ? this.service.isAllowedLocal((new UserSessionModel()).accountId, 'channel.updateChannels') : Promise.resolve(false),
            this.service.getLinkedChannels(this.entityId)
        ])
        .then(res => {
            this.canSearchChannelsByName = res[0];
            this.canLocalize = res[1];
            this.canUpdateChannels = res[2];

            let list = res[3];
            list.forEach((v,i)=>{
                v.checked = false;
            });
            this.channels = list;

            this.wait = false;
        });
    }

    private toggleCheckedAll(e: any){
        this.channels.forEach((v,i)=>{
            if (v.displayStatus !== 'DELETED'){
                v.checked   = e.target.checked;
            }
        });
    }

    private checkBoxCancelBubbling(e: any){
        e.stopPropagation();
    }

    private showPopup(e: any){
        e.preventDefault();
        e.stopPropagation();

        if (this.channels.length){
            this.channels.forEach(v => {
                this.channelsLink.push(Object.assign({}, v));
            });
        }

        this.hideAutocomplete();
        this.popupVisible   = true;

        setImmediate(() => {
            this.textInputEl.nativeElement.focus();
        });
    }

    public popupHide(e?: any){
        this.hideAutocomplete();
        this.popupVisible   = false;
        this.popupWait      = false;

        this.clearTextarea();
        this.channelsLink   = [];
    }

    public popupSave(e?: any){
        this.popupWait  = true;
        this.expressionError    = null;

        this.service
            .linkChannels(this.entityId,
                          this.channelsLink.filter(f => { return f.displayStatus !== 'DELETED' && f.id != this.specialChannelId })
                              .map(v => {return v.id}),
                          this.doLinkSpecialChannel
            )
            .then(newStatus => {
                this.onStatusChange.emit(newStatus);
                this.popupHide();
                this.loadChannelsList();
            })
            .catch(e => {
                this.expressionError    = e.json()['expression'];
                this.popupHide();
            });
    }

    public textInputChange(e: any){
        if (this.popupWait) return;

        let textarea    = this.textInputEl.nativeElement;

        if (this.inputTimer) clearTimeout(this.inputTimer);

        this.inputTimer = setTimeout(()=>{
            let text    = textarea.value;

            this.hideAutocomplete();

            if (text.length >= 3){
                this.popupWait  = true;

                if (~text.indexOf('\n')){
                    let rows            = text.split('\n'),
                        rows_formatted  = rows.map(v => {
                            let parts   = v.split('|');
                            return {
                                name: parts[0] || null,
                                accountName: parts[1] || null
                            };
                        });
                    this.channelService
                        .channelsSearch(this.accountId, rows_formatted)
                        .then(list => {
                            list    = this.removeDuplicates(list);
                            this.channelsLink.push(...list);
                            this.popupWait      = false;

                            setImmediate(() => {
                                this.textInputEl.nativeElement.value    = rows.filter(v => {
                                    return !this.channelsLink.find(f => {
                                        return f.name + '|' + f.accountName === v;
                                    });
                                }).join('\n');
                                this.textInputEl.nativeElement.focus();
                            });
                        });
                } else {
                    this.channelService
                        .getAccountChannels(this.accountId, text.split('|')[0])
                        .then(list  => {
                            this.autocomplete   = this.removeDuplicates(list);
                            this.popupWait      = false;

                            setImmediate(() => {
                                this.textInputEl.nativeElement.focus();
                            });
                        });
                }
            }
        }, 500);
    }

    private removeDuplicates(list: Array<any>): Array<any>{
        if (list.length && this.channelsLink.length){ // remove dublicates
            return list.filter(v => {
                return !this.channelsLink.find(f => {
                    return f.id === v.id;
                });
            });
        } else {
            return list;
        }
    }

    public hideAutocomplete(e?: any){
        this.autocomplete   = null;
    }

    private addChannel(e: any, channel: any){
        e.preventDefault();

        this.channelsLink.push(channel);

        this.clearTextarea();
        this.hideAutocomplete();
    }

    private removeChannel(e: any, id: number){
        e.preventDefault();

        this.channelsLink   = this.channelsLink.filter(v => {
            return v.id !== id;
        })
    }

    private clearTextarea(){
        this.textInputEl.nativeElement.value    = '';
        this.textInputEl.nativeElement.focus();
    }

    public deleteChannels(e?: any, channelId?: number): void {
        let ids: Array<number> = [];
        let doLinkSpecialChannel;
        if (channelId === undefined){
            doLinkSpecialChannel = this.doLinkSpecialChannel;
            this.channels.forEach(v => {
                if (v.checked) {
                    if (v.id == this.specialChannelId) {
                        doLinkSpecialChannel = false;
                    }
                } else if (v.displayStatus !== 'DELETED' && v.id != this.specialChannelId){
                    ids.push(v.id);
                }
            });
        } else {
            doLinkSpecialChannel = this.doLinkSpecialChannel && channelId != this.specialChannelId;
            this.channels.forEach(v => {
                if (v.id !== channelId && v.id != this.specialChannelId && v.displayStatus !== 'DELETED') {
                    ids.push(v.id);
                }
            });
        }

        this.wait = true;
        this.expressionError = null;
        this.service
            .linkChannels(this.entityId, ids, doLinkSpecialChannel)
            .then(newStatus => {
                this.onStatusChange.emit(newStatus);
                this.loadChannelsList();

                setImmediate(() => {
                    this.onTableChange();
                });
            })
            .catch(e => {
                this.expressionError    = e.json()['expression'];
                this.popupHide();
                this.loadChannelsList();
            });
    }

    private onTableChange(e?: any){
        let chekedAll   = true,
            chbx        = this.checkAllInput.nativeElement;

        if (this.channels.length === 0) return;

        this.channels.forEach(v => {
            if (v.displayStatus !== 'DELETED'){
                chekedAll   = chekedAll && v.checked;
            }
        });
        chbx.checked  = chekedAll;
    }

    private changeStatus(channel: any){
        this.channelService
            .statusChange(channel.id, channel.statusChangeOperation, this.flightId, this.lineItemId)
            .then(newStatus => {
                channel.displayStatus = newStatus.channelDisplayStatus;
                this.onStatusChange.emit(newStatus);
            });
    }

    private showBehavioralStats(e: any, channelId: number){

        this.showStats  = 'behavioral';
        this.waitStats  = true;
        this.channelService.getChannelStats('behavioral', channelId)
            .then(stats => {
                this.behavioralStats    = stats;
                this.waitStats          = false;
            })
            .catch(e => {
                this.showStats  = null;
                this.waitStats  = false;
            });
    }

    public hideStatsPopup(){
        this.showStats  = null;
    }

    private showExpressionStats(e: any, channelId: number){

        this.showStats  = 'expression';
        this.waitStats  = true;
        this.channelService.getChannelStats('expression', channelId)
            .then(stats => {
                this.expressionStats    = stats;
                this.waitStats          = false;
            })
            .catch(e => {
                this.showStats  = null;
                this.waitStats  = false;
            });
    }

    public showChannelTree(e: any): void {
        if (e) {
            e.preventDefault();
            e.stopPropagation();
        }

        this.doLinkSpecialChannelTreeFlag = this.doLinkSpecialChannel || !this.channels || this.channels.length == 0;
        this.showChannelTreeFlag = true;
    }

    public onChannelTreeClose(): void {
        this.showChannelTreeFlag = false;
    }

    public onChannelTreeSave(selectedChannels: Array<IdName>): void {
        this.wait = true;
        this.showChannelTreeFlag = false;
        this.expressionError = null;

        this.doLinkSpecialChannel = this.doLinkSpecialChannelTreeFlag;
        this.service.linkChannels(this.entityId,
                                  selectedChannels.map(channel => channel.id).filter(id => id != this.specialChannelId),
                                  this.doLinkSpecialChannel)
            .then(newStatus => {
                this.onStatusChange.emit(newStatus);
                this.wait = false;
                this.loadChannelsList();
            })
            .catch(e => {
                this.wait = false;
                this.expressionError = e.json()['expression'];
            });
    }

    public getSelectedChannelIdNames(): Array<IdName> {
        return this.channels
            .filter( c => c.displayStatus !== 'DELETED' )
            .map( c => new IdName(c.id, c.channelName) );
    }

    public getChannelSources(): Array<string> {
        let result = this.externalChannelSources
                    .split(',')
                    .map( s => s.trim() );
        result.push(this.ownChannelSource.trim());
        return result;
    }

    public showDynamicLocalizationsPopup(e: any, channelId: number) : void {
        this.dynamicLocalizationsChannelId = channelId;
    }

    public onDynamicLocalizationsClose(): void {
        this.dynamicLocalizationsChannelId = null;
    }

    public onDynamicLocalizationsSave(onrejected?: any): void {
        if (onrejected) {
            console.warn('Dynamic Localization is failed for channel id: ' + this.dynamicLocalizationsChannelId);
        } else {
            this.loadChannelsList();
        }
        this.dynamicLocalizationsChannelId = null;
    }
}
