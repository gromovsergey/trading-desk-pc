import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {DropdownButtonMenuItem} from '../../../shared/components/dropdown-button/dropdown-button.component';
import {AgencyService} from '../../services/agency.service';
import {AgencyComponent} from '../agency/agency.component';
import {FileService} from '../../../shared/services/file.service';
import {ChannelService} from '../../../channel/services/channel.service';
import {L10nStatic} from '../../../shared/static/l10n.static';
import {MatDialog} from '@angular/material/dialog';


@Component({
  selector: 'ui-agency-channels',
  templateUrl: './agency-channel.component.html',
  styleUrls: ['./agency-channel.component.scss']
})
export class AgencyChannelsComponent extends AgencyComponent implements OnInit {

  wait2: boolean;
  canCreateChannel: boolean;
  canUpdateChannels: boolean;
  canDownloadReports: boolean;
  createMenu: DropdownButtonMenuItem[];
  channels: any[];

  constructor(protected agencyService: AgencyService,
              protected fileService: FileService,
              protected channelService: ChannelService,
              protected route: ActivatedRoute,
              protected dialog: MatDialog) {
    super(agencyService, fileService, route, dialog);
  }

  ngOnInit(): void {
    this.wait2 = true;
    this.onInit();

    this.promise.then(() => {
      this.createMenu = [
        new DropdownButtonMenuItem(L10nStatic.translate('channel.blockName.behavioural'),
          {link: '/channel/behavioral/add/' + this.agency.id}),
        new DropdownButtonMenuItem(L10nStatic.translate('channel.blockName.expression'),
          {link: '/channel/expression/add/' + this.agency.id})
      ];

      return Promise.all([
        this.channelService.getChannels(this.agency.id),
        this.agencyService.isAllowed(this.agency.id, 'channel.create'),
        this.agencyService.isAllowedLocal(this.agency.id, 'channel.updateChannels'),
        this.agencyService.isAllowedLocal(this.agency.id, 'channel.downloadReport')
      ]);
    }).then(res => {
      this.channels = res[0];
      this.canCreateChannel = Boolean(res[1].allowed);
      this.canUpdateChannels = res[2];
      this.canDownloadReports = res[3];
      this.wait2 = false;
    });
  }
}
