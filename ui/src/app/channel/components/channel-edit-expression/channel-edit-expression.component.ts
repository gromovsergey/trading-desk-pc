import {Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Subscription} from 'rxjs';
import {AgencySessionModel} from '../../../agency/models/agency-session.model';
import {ChannelService} from '../../services/channel.service';
import {ExpressionChannel} from '../../models/expression_channel.model';
import {UserSessionModel} from '../../../user/models/user-session.model';
import {AdvertiserService} from '../../../advertiser/services/advertiser.service';
import {AgencyService} from '../../../agency/services/agency.service';
import {ChannelSearchSessionModel} from '../../models/channel-search-session.model';
import {AdvertiserSessionModel} from '../../../advertiser/models';
import {L10nStatic} from '../../../shared/static/l10n.static';
import {ErrorHelperStatic} from '../../../shared/static/error-helper.static';
import {environment} from '../../../../environments/environment';
import {MatDialog} from '@angular/material/dialog';
import {ChannelTreeComponent} from '../channel-tree/channel-tree.component';

@Component({
  selector: 'ui-channel-edit-expression',
  templateUrl: './channel-edit-expression.component.html',
  styleUrls: ['./channel-edit-expression.component.scss']
})
export class ChannelEditExpressionComponent implements OnInit, OnDestroy {

  @ViewChild('textInput') textInputEl: ElementRef;

  externalChannelSources = environment._EXTERNAL_CHANNEL_SOURCES_;
  ownChannelSource = environment._OWN_CHANNEL_SOURCE_;
  backUrl: string;
  queryBackUrl: string;
  mode: string;
  title: string;
  wait = true;
  waitAccounts = true;
  waitSubmit = false;
  errors: any = {};
  channel: ExpressionChannel;
  accountId: number;
  accounts: any[];
  popupOptions;
  popupVisible = false;
  popupWait = false;
  autocomplete: any[];
  channelsLink: any[];
  showChannelTreeFlag = false;
  matcher = ErrorHelperStatic.getErrorMatcher;

  private channelSearchSession: ChannelSearchSessionModel = new ChannelSearchSessionModel();
  private internalChannelFlag = false;
  private routerSubscription: Subscription;
  private routerQuerySubscription: Subscription;
  private agencySessionModel: AgencySessionModel = new AgencySessionModel();
  private advertiserSessionModel: AdvertiserSessionModel = new AdvertiserSessionModel();
  private inputTimer;
  private currentIdx: number;
  private isCurrentExcluded = false;
  private user: UserSessionModel = new UserSessionModel();

  constructor(private channelService: ChannelService,
              private advertiserService: AdvertiserService,
              private agencyService: AgencyService,
              private route: ActivatedRoute,
              private router: Router,
              private dialog: MatDialog) {

    this.popupOptions = {
      title: L10nStatic.translate('channel.expression.linkChannels'),
      hint: L10nStatic.translate('channel.expression.linkChannels.hint'),
      btnTitle: L10nStatic.translate('button.save'),
      btnIconDisabled: false
    };
  }

  ngOnInit(): void {
    this.routerQuerySubscription = this.route.queryParams.subscribe(params => {
      this.queryBackUrl = params && params.backUrl ? params.backUrl : null;
    });

    this.routerSubscription = this.route.url.subscribe(params => {
      const path = params[1].path;
      if (path === 'add') {
        this.mode = 'add';
        this.channel = new ExpressionChannel();
        this.initAccount(params[2] ? +params[2].path : null);
        this.wait = false;
      } else {
        this.mode = 'edit';
        this.channelService.getExpressionById(+path)
          .then(foundChannel => {
            this.channel = foundChannel;
            this.initAccount(this.channel.accountId);
            this.wait = false;
          });
      }
    });
  }

  initAccount(accountId: number): void {
    if (!accountId) {
      this.internalChannelFlag = true;
      this.channelService.getInternalAccounts()
        .then(accounts => {
          this.accounts = accounts;
          this.channel.accountId = this.user.accountId;
          this.channel.country = 'RU';
          this.backUrl = `/channel/search`;
          this.waitAccounts = false;
        });
    } else {
      this.accountId = +accountId;
      this.channelService.getAccountById(this.accountId)
        .then(account => {
          if (this.mode === 'add') {
            this.channel.accountId = this.accountId;
            this.channel.country = account.countryCode;
          }

          if (account.role === 'ADVERTISER') {
            this.advertiserService.getById(this.accountId).toPromise()
              .then(advertiserAccount => {
                this.advertiserSessionModel.data = advertiserAccount;
                AgencySessionModel.clear();
                this.backUrl = `/advertiser/${this.advertiserSessionModel.id}/channels`;
              });
          } else if (account.role === 'AGENCY') {
            this.agencyService.getById(this.accountId)
              .then(agencyAccount => {
                this.agencySessionModel.data = agencyAccount;
                this.advertiserSessionModel.clear();
                this.backUrl = `/agency/${this.agencySessionModel.id}/channels`;
              });
          } else {
            this.internalChannelFlag = true;
            this.backUrl = `/channel/search`;
          }

          this.waitAccounts = false;
        });
    }
  }

  ngOnDestroy(): void {
    if (this.routerSubscription) {
      this.routerSubscription.unsubscribe();
    }
    if (this.routerQuerySubscription) {
      this.routerQuerySubscription.unsubscribe();
    }
  }

  submitForm(): void {
    this.waitSubmit = true;

    let promise;

    if (this.mode === 'add') {
      if (this.internalChannelFlag) {
        this.channel.visibility = 'PUB';
      }
      promise = this.channelService.createExpression(this.channel);
    } else {
      promise = this.channelService.updateExpression(this.channel);
    }

    promise
      .then(() => {
        if (this.internalChannelFlag) {
          const channelSearchModel: ChannelSearchModel = {
            accountId: this.channel.accountId.toString(),
            visibility: null,
            channelType: null,
            name: this.channel.name
          };
          this.channelSearchSession.setData(channelSearchModel);
        }
        this.waitSubmit = false;
        this.router.navigateByUrl(this.getBackUrl()).catch(err => console.error(err));
      })
      .catch(err => {
        if (err.status === 412) {
          this.errors = ErrorHelperStatic.matchErrors(err);
          this.waitSubmit = false;
        }
      });
  }

  getChannelName(channel: any): void {
    return this.internalChannelFlag ? channel.name :
      channel.localizedName;
  }

  show(): void {
    if (this.internalChannelFlag) {
      this.showPopup();
    } else {
      this.showChannelTree();
    }
  }

  setChannelsLink(list, idx, exclude): void {
    if(exclude){
      this.channel.excludedChannels[idx] = list
    }else{
      this.channel.includedChannels[idx] = list
    }
  }

  addAudience(): void {
    const currentIdx = this.channel.includedChannels.length;
    const isCurrentExcluded = false;
    const channelsLink = [];
    this.dialog.open(ChannelTreeComponent, {
      data: {
        currentIdx,
        isCurrentExcluded,
        channelsLink,
        setChannelsLink: this.setChannelsLink,
        channel: this.channel
      },
      minWidth: 360,
    });
  }

  editAudience(idx: number): void {
    this.currentIdx = idx;
    this.isCurrentExcluded = false;
    this.channelsLink = this.channel.includedChannels[idx].slice();
    this.dialog.open(ChannelTreeComponent, {
      data: {
        currentIdx: idx,
        isCurrentExcluded: false,
        channelsLink: this.channel.includedChannels[idx].slice(),
        setChannelsLink: this.setChannelsLink,
        channel: this.channel
      },
      minWidth: 360,
    });
    // this.show();
  }

  deleteAudience(idx: number): void {
    this.channel.includedChannels.splice(idx, 1);
  }

  addExcludedAudience(): void {
    this.currentIdx = this.channel.excludedChannels.length,
    this.isCurrentExcluded = true;
    this.channelsLink =[]
    this.dialog.open(ChannelTreeComponent, {
      data: {
        currentIdx: this.channel.excludedChannels.length,
        isCurrentExcluded: this.isCurrentExcluded,
        channelsLink: [],
        setChannelsLink: this.setChannelsLink,
        channel: this.channel
      },
      minWidth: 360,
    });
  }

  editExcludedAudience(idx: number): void {
    this.currentIdx = idx;
    this.isCurrentExcluded = true;
    this.channelsLink = this.channel.excludedChannels[idx].slice();
    this.dialog.open(ChannelTreeComponent, {
      data: {
        currentIdx: this.currentIdx,
        isCurrentExcluded: this.isCurrentExcluded,
        channelsLink: this.channelsLink,
        setChannelsLink: this.setChannelsLink,
        channel: this.channel
      },
      minWidth: 360,
    });
  }

  deleteExcludedAudience(idx: number): void {
    this.channel.excludedChannels.splice(idx, 1);
  }

  showPopup(): void {
    this.hideAutocomplete();
    this.popupVisible = true;

    window.setTimeout(() => {
      this.textInputEl.nativeElement.focus();
    });
  }

  hideAutocomplete(): void {
    this.autocomplete = null;
  }

  removeDuplicates(list: Array<any>): Array<any> {
    if (list.length && this.channelsLink.length) {
      return list.filter(v => !this.channelsLink.find(f => f.id === v.id));
    } else {
      return list;
    }
  }

  clearTextarea(): void {
    this.textInputEl.nativeElement.value = '';
    this.textInputEl.nativeElement.focus();
  }

  popupSave(): void {
    this.popupWait = true;
    this.processLinkedChannels();
    this.popupHide();
  }

  processLinkedChannels(): void {
    if (!this.isCurrentExcluded) {
      if (this.channelsLink.length === 0) {
        this.channel.includedChannels.splice(this.currentIdx, 1);
      } else {
        this.channel.includedChannels[this.currentIdx] = this.channelsLink;
      }
    } else {
      if (this.channelsLink.length === 0) {
        this.channel.excludedChannels.splice(this.currentIdx, 1);
      } else {
        this.channel.excludedChannels[this.currentIdx] = this.channelsLink;
      }
    }
  }

  popupHide(): void {
    this.hideAutocomplete();
    this.popupVisible = false;
    this.popupWait = false;

    this.clearTextarea();
    this.channelsLink = [];
  }

  showChannelTree(): void {
    this.showChannelTreeFlag = true;
  }

  onChannelTreeClose(): void {
    this.showChannelTreeFlag = false;
    this.channelsLink = [];
  }

  onChannelTreeSave(selectedChannels: Array<IdName>): void {
    this.wait = true;
    this.channelService.getExternalChannels(this.accountId, selectedChannels.map(c => c.id))
      .then(channels => {
        this.channelsLink = channels;
        this.processLinkedChannels();
        this.onChannelTreeClose();
        this.wait = false;
      });
  }

  getSelectedChannelIdNames(): IdName[] {
    return this.channelsLink.map(c => ({
      id: c.id,
      name: c.name
    }));
  }

  getChannelSources(): string[] {
    const result = this.externalChannelSources
      .split(',')
      .map(s => s.trim());
    result.push(this.ownChannelSource.trim());
    return result;
  }

  getBackUrl(): string {
    return this.queryBackUrl ? this.queryBackUrl : this.backUrl;
  }
}
