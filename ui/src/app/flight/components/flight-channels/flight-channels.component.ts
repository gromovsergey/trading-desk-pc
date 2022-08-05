import {Component, OnChanges, Input, Output, ViewChild, ElementRef, EventEmitter, ContentChild} from '@angular/core';
import {DropdownButtonMenuItem} from '../../../shared/components/dropdown-button/dropdown-button.component';
import {moment} from '../../../common/common.const';
import {L10nChannelTypes} from '../../../common/L10n.const';
import {LineItemService} from '../../../lineitem/services/lineitem.service';
import {AgencySessionModel} from '../../../agency/models/agency-session.model';
import {ChannelService} from '../../../channel/services/channel.service';
import {FlightService} from '../../services/flight.service';
import {UserSessionModel} from '../../../user/models/user-session.model';
import {AdvertiserSessionModel} from '../../../advertiser/models';
import {L10nStatic} from '../../../shared/static/l10n.static';
import {MatTableDataSource} from '@angular/material/table';
import {SelectionModel} from '@angular/cdk/collections';
import {MatPaginator} from '@angular/material/paginator';
import {ErrorHelperStatic} from '../../../shared/static/error-helper.static';
import {environment} from '../../../../environments/environment';
import {MatDialog} from '@angular/material/dialog';
import {ChannelDynamicLocalizationComponent}
  from '../../../shared/components/channel-dynamic-localization/channel-dynamic-localization.component';
import {ChannelBehavioralStatsComponent} from '../../../channel/components/channel-behavioral-stats/channel-behavioral-stats.component';
import {ChannelExpressionStatsComponent} from '../../../channel/components/channel-expression-stats/channel-expression-stats.component';
import {FlightChannelsTreeComponent} from '../flight-channels-tree/flight-channels-tree.component';
import {take} from 'rxjs/operators';
import {ChannelTreeComponent} from "../../../channel/components/channel-tree/channel-tree.component";
import {FlightChannelTreeSearchComponent} from "../flight-channel-tree-search/flight-channel-tree-search.component";


@Component({
  selector: 'ui-flight-channels',
  templateUrl: './flight-channels.component.html',
  styleUrls: ['./flight-channels.component.scss']
})
export class FlightChannelsComponent implements OnChanges {

  @Input() flightId: number;
  @Input() lineItemId: number;
  @Input() linkSpecialChannelFlag: boolean;
  @Input() specialChannelId: number;
  @Input() short: boolean;
  @Input() readonly = false;
  @Output() statusChange = new EventEmitter();
  @Output() error = new EventEmitter();
  @ViewChild('textInput') textInputEl: ElementRef;
  @ViewChild('checkAll') checkAllInput: ElementRef;
  @ContentChild('channelsNotifications') channelsNotificationsEl: ElementRef;
  @ViewChild('channelsNotifications') channelsNotificationsChildEl: ElementRef;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  wait = true;
  channels: MatTableDataSource<any>;
  bulkMenu = [];
  popupVisible = false;
  popupWait = false;
  canSearchChannelsByName = false;
  canLocalize = false;
  canUpdateChannels = false;
  inputTimer;
  accountId: number;
  autocomplete: any[];
  channelsLink: any[] = [];
  expressionError: string[];
  moment = moment;
  service;
  entityId: number;
  L10nChannelTypes = L10nChannelTypes;
  showChannelTreeFlag = false;
  doLinkSpecialChannel: boolean;
  selection = new SelectionModel<any>(true, []);

  get displayedColumns(): string[] {
    return [...(!this.readonly ? ['select'] : []), 'channel', 'localization', 'channelType',
      ...(!this.short ? ['imps', 'clicks', 'ctr'] : []), 'dailyAudience', 'action'];
  }

  constructor(
    private flightService: FlightService,
    private lineItemService: LineItemService,
    private channelService: ChannelService,
    private dialog: MatDialog) {

    const agency = new AgencySessionModel();
    const advertiser = new AdvertiserSessionModel();

    this.accountId = agency.hasData() ? agency.id : advertiser.id;
    this.bulkMenu.push(
      new DropdownButtonMenuItem(L10nStatic.translate('button.unlink'), {onclick: this.deleteChannels.bind(this)})
    );
  }

  ngOnChanges(): void {
    if (this.channelsNotificationsChildEl && this.channelsNotificationsEl) {
      this.channelsNotificationsChildEl.nativeElement.appendChild(this.channelsNotificationsEl.nativeElement);
    }

    this.service = this.flightId ? this.flightService : this.lineItemService;
    this.entityId = this.flightId ? this.flightId : this.lineItemId;
    this.doLinkSpecialChannel = this.linkSpecialChannelFlag;

    this.loadChannelsList();
  }

  loadChannelsList(): void {
    const isInternal = new UserSessionModel().isInternal();
    this.wait = true;

    Promise.all([
      this.service.isAllowedLocal0('channel.search'),
      isInternal ? this.service.isAllowedLocal0('localization.update') : Promise.resolve(false),
      isInternal ?
        this.service.isAllowedLocal((new UserSessionModel()).accountId, 'channel.updateChannels') :
        Promise.resolve(false),
      this.service.getLinkedChannels(this.entityId)
    ])
      .then(res => {
        this.canSearchChannelsByName = res[0];
        this.canLocalize = res[1];
        this.canUpdateChannels = res[2];
        this.channels = new MatTableDataSource<any>(res[3]);
        this.wait = false;

        window.setTimeout(() => {
          this.channels.paginator = this.paginator;
        });
      });
  }

  popupSave(channelsLink): void {
    this.popupWait = true;
    this.expressionError = null;

    this.service.linkChannels(
      this.entityId,
      channelsLink.filter(f => f.displayStatus !== 'DELETED' && f.id !== this.specialChannelId).map(v => v.id),
      this.doLinkSpecialChannel
    )
      .then(newStatus => {
        this.statusChange.emit(newStatus);
        this.loadChannelsList();
      })
      .catch(err => {
        this.expressionError = ErrorHelperStatic.matchErrors(err).expression;
      });
  }

  showSearchByName(e: any): void {
    e.preventDefault();
    e.stopPropagation();

    this.dialog.open(FlightChannelTreeSearchComponent, {
      data: {
        accountId: this.accountId,
        channels: this.channels.data,
        popupSave: this.popupSave.bind(this)
      },
      minWidth: 360,
    });

    this.hideAutocomplete();
    this.popupVisible = true;
  }

  popupHide(): void {
    this.hideAutocomplete();
    this.popupVisible = false;
    this.popupWait = false;

    this.clearTextarea();
    this.channelsLink = [];
  }


  removeDuplicates(list: Array<any>): Array<any> {
    if (list.length && this.channelsLink.length) { // remove dublicates
      return list.filter(v => !this.channelsLink.find(f => f.id === v.id));
    } else {
      return list;
    }
  }

  hideAutocomplete(e?: any): void {
    this.autocomplete = null;
  }

  addChannel(e: any, channel: any): void {
    e.preventDefault();

    this.channelsLink.push(channel);

    this.clearTextarea();
    this.hideAutocomplete();
  }

  removeChannel(e: any, id: number): void {
    e.preventDefault();

    this.channelsLink = this.channelsLink.filter(v => v.id !== id);
  }

  clearTextarea(): void {
    this.textInputEl.nativeElement.value = '';
    this.textInputEl.nativeElement.focus();
  }

  deleteChannels(e?: any, channelId?: number): void {
    const ids: Array<number> = [];
    let doLinkSpecialChannel;
    if (channelId === undefined) {
      doLinkSpecialChannel = this.doLinkSpecialChannel;
      this.channels.data.forEach(v => {
        if (v.checked) {
          if (+v.id === +this.specialChannelId) {
            doLinkSpecialChannel = false;
          }
        } else if (v.displayStatus !== 'DELETED' && +v.id !== +this.specialChannelId) {
          ids.push(v.id);
        }
      });
    } else {
      doLinkSpecialChannel = this.doLinkSpecialChannel && +channelId !== +this.specialChannelId;
      this.channels.data.forEach(v => {
        if (+v.id !== +channelId && +v.id !== +this.specialChannelId && v.displayStatus !== 'DELETED') {
          ids.push(v.id);
        }
      });
    }

    this.wait = true;
    this.expressionError = null;
    this.service
      .linkChannels(this.entityId, ids, doLinkSpecialChannel)
      .then(newStatus => {
        this.statusChange.emit(newStatus);
        this.loadChannelsList();
      })
      .catch(err => {
        this.expressionError = ErrorHelperStatic.matchErrors(err).expression;
        this.popupHide();
        this.loadChannelsList();
      });
  }

  changeStatus(channel: any): void {
    this.channelService
      .statusChange(channel.id, channel.statusChangeOperation, this.flightId, this.lineItemId)
        .catch((error) => {
          this.error.emit(error);
        })
      .then(newStatus => {
        channel.displayStatus = newStatus.channelDisplayStatus;
        this.statusChange.emit(newStatus);
      });
  }

  showBehavioralStats(channelId: number): void {
    this.dialog.open(ChannelBehavioralStatsComponent, {
      minWidth: 360,
      data: {channelId},
    });
  }

  showExpressionStats(channelId: number): void {
    this.dialog.open(ChannelExpressionStatsComponent, {
      minWidth: 360,
      data: {channelId},
    });
  }

  showChannelTree(): void {
    const linkSpecial = this.doLinkSpecialChannel || !this.channels || this.channels.data.length === 0;
    this.dialog.open(FlightChannelsTreeComponent, {
      minWidth: 500,
      data: {
        accountId: this.accountId,
        linkSpecial,
        selectedChannels: this.getSelectedChannelIdNames(),
      }
    })
      .afterClosed()
      .pipe(take(1))
      .subscribe(res => this.channelTreeClose(res));
  }

  async channelTreeClose(saveData: any): Promise<any> {
    if (saveData) {
      this.wait = true;
      this.showChannelTreeFlag = false;
      this.expressionError = null;
      this.doLinkSpecialChannel = saveData.linkSpecial;

      try {
        const status = await this.service.linkChannels(
          this.entityId,
          saveData.checkedIds.filter(id => id !== this.specialChannelId),
          this.doLinkSpecialChannel
        );
        this.statusChange.emit(status);
      } catch (err) {
        this.expressionError = ErrorHelperStatic.matchErrors(err).expression;
      } finally {
        this.wait = false;
        this.loadChannelsList();
      }
    }
  }

  getSelectedChannelIdNames(): IdName[] {
    return this.channels.data
      .filter(c => c.displayStatus !== 'DELETED')
      .map(c => ({
        id: c.id,
        name: c.channelName
      }));
  }

  showDynamicLocalizationsPopup(channelId: number): void {
    this.dialog.open(ChannelDynamicLocalizationComponent, {
      minWidth: 360,
      data: {channelId},
    });
  }

  isAllSelected(): boolean {
    const numSelected = this.selection.selected.length;
    const numRows = this.channels.data.length;
    return numSelected === numRows;
  }

  masterToggle(): void {
    if (this.isAllSelected()) {
      this.selection.clear();
    } else {
      this.channels.data.forEach(row => this.selection.select(row));
    }
  }
}
