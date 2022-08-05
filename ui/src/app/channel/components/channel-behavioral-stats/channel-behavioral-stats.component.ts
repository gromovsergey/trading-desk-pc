import {Component, Inject, OnInit} from '@angular/core';
import {ChannelService} from '../../services/channel.service';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {dateFormatShort, moment} from '../../../common/common.const';

@Component({
  selector: 'ui-channel-behavioral-stats',
  templateUrl: 'channel-behavioral-stats.component.html',
  styleUrls: ['./channel-behavioral-stats.component.scss']
})
export class ChannelBehavioralStatsComponent implements OnInit {

  wait: boolean;
  stats: any;
  moment = moment;
  dateFormatShort = dateFormatShort;
  displayColumnsHits = ['keyword', 'hits', 'type'];
  displayColumnsTypeHits = ['date', 'pageHits', 'searchHits', 'urlHits', 'urlKeywordsHits', 'totalHits',
    'totalUniques', 'activeDailyUniques', 'bids', 'clicks'];

  constructor(private channelService: ChannelService,
              @Inject(MAT_DIALOG_DATA) public data: any) {
  }

  ngOnInit(): void {
    this.wait = true;
    this.channelService.getChannelStats('behavioral', this.data.channelId)
      .then(stats => {
        this.stats = stats;
        this.wait = false;
      })
      .catch(() => {
        this.wait = false;
      });
  }
}
