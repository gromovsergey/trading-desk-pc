import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {DropdownButtonMenuItem} from '../shared/dropdown_button.component';
import {AgencyService} from '../agency/agency.service';
import {AdvertiserService} from './advertiser.service';
import {AdvertiserComponent} from './advertiser.component';
import {FileService} from "../shared/file.service";
import {ChannelService} from "../channel/channel.service";

@Component({
    selector: 'ui-advertiser-channels',
    templateUrl: 'channels.html'
})

export class AdvertiserChannelsComponent extends AdvertiserComponent implements OnInit{

    public title: string;
    protected titlePrefix: string;

    private createMenu: Array<DropdownButtonMenuItem>;
    public _wait: boolean;
    private canCreateChannel: boolean;
    private canUpdateChannels: boolean;
    private canDownloadReports: boolean;

    public channels: Array<any>;

    constructor(protected advertiserService: AdvertiserService,
                protected agencyService: AgencyService,
                protected fileService: FileService,
                protected channelService: ChannelService,
                protected route: ActivatedRoute) {
        super(advertiserService, agencyService, fileService, route);
    }

    protected initResources(): void {
        this.title = '_L10N_(advertiserAccount.advertiserChannels)';
        this.titlePrefix = '_L10N_(advertiserAccount.advertiserChannels)' + ': ';
    }

    ngOnInit() {
        this._wait = true;
        this.onInit();

        this.promise.then(() => {
            this.createMenu = [
                new DropdownButtonMenuItem('_L10N_(channel.blockName.behavioural)', {link: '/channel/behavioral/add/' + this.advertiser.id}),
                new DropdownButtonMenuItem('_L10N_(channel.blockName.expression)', {link: '/channel/expression/add/' + this.advertiser.id})
            ];

            return Promise.all([
                this.channelService.getChannels(this.advertiser.id),
                this.advertiserService.isAllowed(this.advertiser.id, 'channel.create'),
                this.advertiserService.isAllowedLocal(this.advertiser.id, 'channel.updateChannels'),
                this.agencyService.isAllowedLocal(this.advertiser.id, 'channel.downloadReport')
            ]);
        }).then(res => {
            this.channels = res[0];
            this.canCreateChannel = Boolean(res[1]['allowed']);
            this.canUpdateChannels = res[2];
            this.canDownloadReports = res[3];
            this._wait = false;
        });
    }
}
