import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {DropdownButtonMenuItem} from '../../../shared/components/dropdown-button/dropdown-button.component';
import {AgencyService} from '../../../agency/services/agency.service';
import {AdvertiserService} from '../../services/advertiser.service';
import {FileService} from '../../../shared/services/file.service';
import {ChannelService} from '../../../channel/services/channel.service';
import {L10nStatic} from '../../../shared/static/l10n.static';
import {Observable, of, Subject} from 'rxjs';
import {catchError, filter, map, switchMap, tap} from 'rxjs/operators';

@Component({
  selector: 'ui-advertiser-channels',
  templateUrl: './advertiser-channels.component.html',
  styleUrls: ['./advertiser-channels.component.scss']
})
export class AdvertiserChannelsComponent implements OnInit {

  createMenu: DropdownButtonMenuItem[];
  canCreateChannel: boolean;
  canUpdateChannels: boolean;
  canDownloadReports: boolean;
  loadedSubject$: Subject<number> = new Subject();
  advertiser$: Observable<any>;
  route$: Observable<any>;
  channels$: Observable<any>;

  constructor(protected advertiserService: AdvertiserService,
              protected agencyService: AgencyService,
              protected fileService: FileService,
              protected channelService: ChannelService,
              protected route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.route$ = this.route.paramMap.pipe(
      map(param => +param.get('id')),
      filter(id => !!id),
      tap(id => {
        this.loadLocalRestrictions(id).catch();
        this.createMenu = [
          new DropdownButtonMenuItem(L10nStatic.translate('channel.blockName.behavioural'),
            {link: '/channel/behavioral/add/' + id}),
          new DropdownButtonMenuItem(L10nStatic.translate('channel.blockName.expression'),
            {link: '/channel/expression/add/' + id})
        ];
      })
    );
    this.advertiser$ = this.route$.pipe(
      switchMap(id => this.advertiserService.getById(id)),
      tap(advertiser => this.loadedSubject$.next(advertiser.id))
    );
    this.channels$ = this.route$.pipe(
      switchMap(id => this.channelService.getChannels(id)),
      catchError(err => {
        if (err.status === 403) {
          return of({error: 403});
        }
        return of(null);
      })
    );
  }

  async loadLocalRestrictions(id: number): Promise<any> {
    try {
      const res = await Promise.all([
        this.advertiserService.isAllowed(id, 'channel.create'),
        this.advertiserService.isAllowedLocal(id, 'channel.updateChannels'),
        this.agencyService.isAllowedLocal(id, 'channel.downloadReport')
      ]);
      this.canCreateChannel = Boolean(res[0].allowed);
      this.canUpdateChannels = res[1];
      this.canDownloadReports = res[2];
    } catch (err) {
      console.error(err);
    }
  }
}
