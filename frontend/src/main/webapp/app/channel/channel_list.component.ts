import {Component, Input} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {moment, dateFormatShort} from '../common/common.const';
import {L10nCountries, L10nChannelTypes, L10nChannelVisibilities} from '../common/L10n.const';
import {ChannelService} from './channel.service';


@Component({
    selector: 'ui-channel-list',
    templateUrl: 'channel_list.html'
})
export class ChannelListComponent {

    @Input() channels: Array<any>;
    @Input() canLocalize: boolean = false;
    @Input() showChannels: boolean = true;
    @Input() waitChannels: boolean = false;
    @Input() showAccountColumn: boolean = true;
    @Input() showVisibilityColumn: boolean = true;
    @Input() truncated: boolean = false;
    @Input() canUpdateChannels: boolean;

    protected promise: Promise<any>;

    public moment = moment;
    public dateFormatShort = dateFormatShort;

    private showStats: string;
    private waitStats: boolean = false;
    private behavioralStats: any;
    private expressionStats: any;
    private statsPopup;

    public dynamicLocalizationsChannelId: number = null;

    public L10nCountries = L10nCountries;
    public L10nChannelTypes = L10nChannelTypes;
    public L10nChannelVisibilities = L10nChannelVisibilities;

    constructor(protected channelService: ChannelService,
                protected route: ActivatedRoute) {
        this.statsPopup = {
            title: '_L10N_(channel.blockName.channel.statistics)',
            btnTitle: '',
            btnIcon: null,
            btnIconDisabled: false,
            size: 'lg'
        };
    }

    private statusChange(channel: any) {
        this.channelService
            .statusChange(channel.id, channel.statusChangeOperation)
            .then(newStatus => {
                channel.displayStatus = newStatus.channelDisplayStatus;
            });
    }

    private deleteChannels(e: any, channel: any) {
        this.channelService
            .statusChange(channel.id, 'DELETE')
            .then(newStatus => {
                channel.displayStatus = newStatus.channelDisplayStatus;

                this.channels = this.channels.filter(ch => {
                    return ch.id !== channel.id;
                });

            });
    }

    private showBehavioralStats(e: any, channelId: number) {
        this.showStats = 'behavioral';
        this.waitStats = true;
        this.channelService.getChannelStats('behavioral', channelId)
            .then(stats => {
                this.behavioralStats = stats;
                this.waitStats = false;
            })
            .catch(e => {
                this.showStats = null;
                this.waitStats = false;
            });
    }

    private hideStatsPopup() {
        this.showStats = null;
    }

    private showExpressionStats(e: any, channelId: number) {
        this.showStats = 'expression';
        this.waitStats = true;
        this.channelService.getChannelStats('expression', channelId)
            .then(stats => {
                this.expressionStats = stats;
                this.waitStats = false;
            })
            .catch(e => {
                this.showStats = null;
                this.waitStats = false;
            });
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
        }
        this.dynamicLocalizationsChannelId = null;
    }
}
