import {Component, Inject, OnInit} from '@angular/core';
import {ChannelService} from '../../services/channel.service';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {dateFormatShort, moment} from '../../../common/common.const';

@Component({
  selector: 'ui-channel-expression-stats',
  templateUrl: 'channel-expression-stats.component.html',
  styleUrls: ['./channel-expression-stats.component.scss']
})
export class ChannelExpressionStatsComponent implements OnInit {

  wait: boolean;
  stats: any;
  moment = moment;
  dateFormatShort = dateFormatShort;
  displayColumns = ['date', 'totalUniques', 'activeDailyUniques', 'bids', 'clicks'];

  constructor(private channelService: ChannelService,
              @Inject(MAT_DIALOG_DATA) public data: any) {
  }

  ngOnInit(): void {
    this.wait = true;
    this.channelService.getChannelStats('expression', this.data.channelId)
      .then(stats => {
        this.stats = stats;
        this.wait = false;
      })
      .catch(() => {
        this.wait = false;
      });
  }
}
