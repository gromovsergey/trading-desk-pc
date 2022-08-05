import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Subscription} from 'rxjs';
import {AgencySessionModel} from '../../../agency/models/agency-session.model';
import {ChannelService} from '../../services/channel.service';
import {BehavioralChannel} from '../../models/behavioral-channel.model';
import {AdvertiserService} from '../../../advertiser/services/advertiser.service';
import {AgencyService} from '../../../agency/services/agency.service';
import {UserSessionModel} from '../../../user/models/user-session.model';
import {ChannelSearchSessionModel} from '../../models/channel-search-session.model';
import {AdvertiserSessionModel} from '../../../advertiser/models';
import {ErrorHelperStatic} from '../../../shared/static/error-helper.static';

@Component({
  selector: 'ui-channel-edit-behavioral',
  templateUrl: 'channel-edit-behavioral.component.html',
  styleUrls: ['./channel-edit-behavioral.component.scss']
})
export class ChannelEditBehavioralComponent implements OnInit, OnDestroy {

  wait = true;
  waitAccounts = true;
  waitSubmit = false;
  errors: any = {};
  channel: BehavioralChannel;
  accounts: any[];
  backUrl: string;
  queryBackUrl: string;
  mode: string;
  matcher = ErrorHelperStatic.getErrorMatcher;
  private routerSubscription: Subscription;
  private routerQuerySubscription: Subscription;
  private channelSearchSession: ChannelSearchSessionModel = new ChannelSearchSessionModel();
  private addInternalChannel = false;
  private agencySessionModel: AgencySessionModel = new AgencySessionModel();
  private advertiserSessionModel: AdvertiserSessionModel = new AdvertiserSessionModel();
  private user: UserSessionModel = new UserSessionModel();

  constructor(private channelService: ChannelService,
              private advertiserService: AdvertiserService,
              private agencyService: AgencyService,
              private route: ActivatedRoute,
              private router: Router) {
  }

  ngOnInit(): void {
    this.routerQuerySubscription = this.route.queryParams.subscribe(params => {
      this.queryBackUrl = params && params.backUrl ? params.backUrl : null;
    });

    this.routerSubscription = this.route.url.subscribe(params => {
      const path = params[1].path;
      if (path === 'add') {
        this.mode = 'add';
        this.channel = new BehavioralChannel();
        this.initAccount(params[2] ? +params[2].path : null);
        this.wait = false;
      } else {
        this.mode = 'edit';
        this.channelService.getBehavioralById(+path)
          .then(foundChannel => {
            this.channel = foundChannel;
            this.initAccount(this.channel.account.id);
            this.wait = false;
          });
      }
    });
  }

  initAccount(accountId: number): void {
    if (!accountId) {
      this.addInternalChannel = true;
      this.channelService.getInternalAccounts()
        .then(accounts => {
          this.accounts = accounts;
          this.channel.account.id = this.user.accountId;
          this.channel.country = 'RU';
          this.backUrl = `/channel/search`;
          this.waitAccounts = false;
        });
    } else {
      this.channelService.getAccountById(accountId)
        .then(account => {
          if (this.mode === 'add') {
            this.channel.account.id = +accountId;
            this.channel.country = account.countryCode;
          }

          if (account.role === 'ADVERTISER') {
            this.advertiserService.getById(accountId).toPromise()
              .then(advertiserAccount => {
                this.advertiserSessionModel.data = advertiserAccount;
                AgencySessionModel.clear();
                this.backUrl = `/advertiser/${this.advertiserSessionModel.id}/channels`;
              });
          } else if (account.role === 'AGENCY') {
            this.agencyService.getById(accountId)
              .then(agencyAccount => {
                this.agencySessionModel.data = agencyAccount;
                this.advertiserSessionModel.clear();
                this.backUrl = `/agency/${this.agencySessionModel.id}/channels`;
              });
          } else {
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
      if (this.addInternalChannel) {
        this.channel.visibility = 'PUB';
      }
      promise = this.channelService.createBehavioral(this.channel);
    } else {
      promise = this.channelService.updateBehavioral(this.channel);
    }

    promise
      .then(id => {
        if (this.addInternalChannel) {
          const channelSearchModel: ChannelSearchModel = {
            visibility: null,
            name: this.channel.name,
            accountId: this.channel.account.id.toString(),
            channelType: null
          };
          this.channelSearchSession.setData(channelSearchModel);
        }
        this.waitSubmit = false;
        this.router.navigateByUrl(this.getBackUrl());
      })
      .catch(err => {
        if (err.status === 412) {
          this.errors = ErrorHelperStatic.matchErrors(err);
          this.waitSubmit = false;
        }
      });
  }

  getBackUrl(): string {
    return this.queryBackUrl ? this.queryBackUrl : this.backUrl;
  }
}
