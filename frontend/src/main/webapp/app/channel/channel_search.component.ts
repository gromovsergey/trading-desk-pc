import {Component, OnInit} from '@angular/core';

import {DropdownButtonMenuItem} from '../shared/dropdown_button.component';
import {PageComponent} from "../shared/page.component";
import {ChannelService} from "./channel.service";
import {UserSessionModel} from "../user/user_session.model";
import {ChannelSearchModel, ChannelSearchSessionModel} from "./channel_search_session.model";

@Component({
    selector: 'ui-channel-search',
    templateUrl: 'channel_search.html'
})

export class ChannelSearchComponent extends PageComponent implements OnInit {

    private channelSearchSession: ChannelSearchSessionModel = new ChannelSearchSessionModel();

    public title: string;

    public createMenu: Array<DropdownButtonMenuItem>;
    public wait: boolean;
    public canLocalize: boolean;
    public canCreateChannel: boolean;
    public canUpdateChannels: boolean;

    public accounts: Array<any>;

    public searchModel: ChannelSearchModel = new ChannelSearchModel();

    public channels: Array<any>;
    public truncated: boolean;
    public showChannels: boolean = false;
    public waitChannels: boolean = false;

    constructor(private channelService: ChannelService) {
        super();
        this.initResources();
    }

    protected initResources(): void {
        this.title = '_L10N_(channel.blockName.channels)';

        this.createMenu = [
            new DropdownButtonMenuItem('_L10N_(channel.blockName.behavioural)', {link: '/channel/behavioral/add'}),
            new DropdownButtonMenuItem('_L10N_(channel.blockName.expression)', {link: '/channel/expression/add'})
        ];
    }

    ngOnInit() {
        this.wait = true;

        Promise.all([
            this.channelService.isAllowedLocal0('localization.update'),
            this.channelService.isAllowedLocal0('channel.createInternal'),
            this.channelService.isAllowedLocal((new UserSessionModel()).accountId, 'channel.updateChannels'),
            this.channelService.getChannelOwners()
        ]).then(res => {
            this.canLocalize = Boolean(res[0]);
            this.canCreateChannel = Boolean(res[1]);
            this.canUpdateChannels = Boolean(res[2]);
            this.accounts = res[3];

            if (this.channelSearchSession.hasData()) {
                this.searchModel = this.channelSearchSession.getData();
                this.searchInternal();
            }

            this.wait = false;
        });
    }

    public search() {
        this.channelSearchSession.setData(this.searchModel);
        this.searchInternal();
    }

    private searchInternal() {
        this.waitChannels = true;
        this.channelService.getAllChannels(
            this.searchModel.name.length > 0 ? this.searchModel.name : null,
            this.searchModel.accountId.length > 0 ? +this.searchModel.accountId : null,
            this.searchModel.channelType.length > 0 ? this.searchModel.channelType : null,
            this.searchModel.visibility.length > 0 ? this.searchModel.visibility : null
        ).then(res => {
            this.channels = res['channels'];
            this.truncated = res['truncated'];
            this.showChannels = true;
            this.waitChannels = false;
        });
    }
}
