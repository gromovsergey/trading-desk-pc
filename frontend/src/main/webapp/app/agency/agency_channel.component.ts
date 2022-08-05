import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {DropdownButtonMenuItem} from '../shared/dropdown_button.component';

import {AgencyService} from './agency.service';
import {AgencyComponent} from './agency.component';
import {FileService} from "../shared/file.service";
import {ChannelService} from "../channel/channel.service";


@Component({
    selector: 'ui-agency-channels',
    templateUrl: 'channels.html'
})

export class AgencyChannelsComponent extends AgencyComponent implements OnInit{

    public title: string;
    protected titlePrefix: string;
    public _wait: boolean;
    private canCreateChannel: boolean;
    private canUpdateChannels: boolean;
    private canDownloadReports: boolean;
    private createMenu: Array<DropdownButtonMenuItem>;

    public channels: Array<any>;

    constructor(protected agencyService: AgencyService,
                protected fileService: FileService,
                protected channelService: ChannelService,
                protected route: ActivatedRoute){
        super(agencyService, fileService, route);
    }

    protected initResources(): void {
        this.title = '_L10N_(agencyAccount.channels)';
        this.titlePrefix = '_L10N_(agencyAccount.channels)' + ': ';
    }

    ngOnInit() {
        this._wait = true;
        this.onInit();

        this.promise.then(() => {
            this.createMenu = [
                new DropdownButtonMenuItem('_L10N_(channel.blockName.behavioural)', {link: '/channel/behavioral/add/' + this.agency.id}),
                new DropdownButtonMenuItem('_L10N_(channel.blockName.expression)', {link: '/channel/expression/add/' + this.agency.id})
            ];

            return Promise.all([
                this.channelService.getChannels(this.agency.id),
                this.agencyService.isAllowed(this.agency.id, 'channel.create'),
                this.agencyService.isAllowedLocal(this.agency.id, 'channel.updateChannels'),
                this.agencyService.isAllowedLocal(this.agency.id, 'channel.downloadReport')
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
