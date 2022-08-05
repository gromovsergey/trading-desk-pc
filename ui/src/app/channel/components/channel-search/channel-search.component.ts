import {Component, OnInit} from '@angular/core';
import {DropdownButtonMenuItem} from '../../../shared/components/dropdown-button/dropdown-button.component';
import {ChannelService} from '../../services/channel.service';
import {UserSessionModel} from '../../../user/models/user-session.model';
import {ChannelSearchSessionModel} from '../../models/channel-search-session.model';

@Component({
  selector: 'ui-channel-search',
  templateUrl: 'channel-search.component.html',
  styleUrls: ['./channel-search.component.scss']
})
export class ChannelSearchComponent implements OnInit {

  createMenu: DropdownButtonMenuItem[];
  wait: boolean;
  canLocalize: boolean;
  canCreateChannel: boolean;
  canUpdateChannels: boolean;
  accounts: any[];
  searchModel: ChannelSearchModel;
  channels: any[];
  truncated: boolean;
  showChannels = false;
  waitChannels = false;
  readonly defaultValue = '';
  private channelSearchSession: ChannelSearchSessionModel = new ChannelSearchSessionModel();

  constructor(private channelService: ChannelService) {
    this.searchModel = {
      name: '',
      accountId: '',
      channelType: '',
      visibility: '',
    };
  }


  ngOnInit(): void {
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

  search(): void {
    this.channelSearchSession.setData(this.searchModel);
    this.searchInternal();
  }

  searchInternal(): void {
    this.waitChannels = true;
    this.channelService.getAllChannels(
      this.searchModel.name.length > 0 ? this.searchModel.name : null,
      this.searchModel.accountId.length > 0 ? this.searchModel.accountId : null,
      this.searchModel.channelType.length > 0 ? this.searchModel.channelType : null,
      this.searchModel.visibility.length > 0 ? this.searchModel.visibility : null
    ).then(res => {
      this.channels = res.channels;
      this.truncated = res.truncated;
      this.showChannels = true;
      this.waitChannels = false;
    });
  }
}
