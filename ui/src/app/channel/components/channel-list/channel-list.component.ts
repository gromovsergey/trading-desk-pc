import {Component, Input} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {moment, dateFormatShort} from '../../../common/common.const';
import {L10nChannelTypes, L10nChannelVisibilities} from '../../../common/L10n.const';
import {ChannelService} from '../../services/channel.service';
import {L10nStatic} from '../../../shared/static/l10n.static';
import {MatDialog} from '@angular/material/dialog';
import {ChannelDynamicLocalizationComponent}
  from '../../../shared/components/channel-dynamic-localization/channel-dynamic-localization.component';
import {ChannelBehavioralStatsComponent} from '../channel-behavioral-stats/channel-behavioral-stats.component';
import {ChannelExpressionStatsComponent} from '../channel-expression-stats/channel-expression-stats.component';


@Component({
  selector: 'ui-channel-list',
  templateUrl: 'channel-list.component.html',
  styleUrls: ['./channel-list.component.scss']
})
export class ChannelListComponent {

  @Input() channels: Array<any>;
  @Input() canLocalize = false;
  @Input() showChannels = true;
  @Input() waitChannels = false;
  @Input() showAccountColumn = true;
  @Input() showVisibilityColumn = true;
  @Input() truncated = false;
  @Input() canUpdateChannels: boolean;

  moment = moment;
  statsPopup;
  L10nChannelTypes = L10nChannelTypes;
  L10nChannelVisibilities = L10nChannelVisibilities;
  protected promise: Promise<any>;

  get displayedColumns(): string[] {
    return [...(this.showAccountColumn ? ['account'] : []), 'channel', 'localize', 'channelType',
      ...(this.showVisibilityColumn ? ['visibility'] : []), 'action'];
  }

  constructor(protected channelService: ChannelService,
              protected route: ActivatedRoute,
              private dialog: MatDialog) {
    this.statsPopup = {
      title: L10nStatic.translate('channel.blockName.channel.statistics'),
      btnTitle: '',
      btnIcon: null,
      btnIconDisabled: false,
      size: 'lg'
    };
  }

  statusChange(channel: any): void {
    this.channelService
      .statusChange(channel.id, channel.statusChangeOperation)
      .then(newStatus => {
        channel.displayStatus = newStatus.channelDisplayStatus;
      });
  }

  deleteChannels(e: any, channel: any): void {
    this.channelService
      .statusChange(channel.id, 'DELETE')
      .then(newStatus => {
        channel.displayStatus = newStatus.channelDisplayStatus;

        this.channels = this.channels.filter(ch => ch.id !== channel.id);

      });
  }

  showBehavioralStats(channelId: number): void {
    this.dialog.open(ChannelBehavioralStatsComponent, {
      minWidth: 360,
      data: {channelId}
    });
  }

  showExpressionStats(channelId: number): void {
    this.dialog.open(ChannelExpressionStatsComponent, {
      minWidth: 360,
      data: {channelId}
    });
  }

  showDynamicLocalizationsPopup(channelId: number): void {
    this.dialog.open(ChannelDynamicLocalizationComponent, {
      minWidth: 360,
      data: {channelId},
    });
  }
}
