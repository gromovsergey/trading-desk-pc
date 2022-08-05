import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Data, NavigationEnd, Router} from '@angular/router';
import {AgencySessionModel} from '../../../agency/models/agency-session.model';
import {UserSessionModel} from '../../../user/models/user-session.model';
import {CommonService} from '../../../common/services/common.service';
import {AdvertiserSessionModel} from '../../../advertiser/models';
import {Subscription, zip} from 'rxjs';
import {filter, map} from 'rxjs/operators';
import {environment} from "../../../../environments/environment";

@Component({
  selector: 'ui-main',
  styleUrls: ['./menu-main.component.scss'],
  templateUrl: './menu-main.component.html'
})
export class MenuMainComponent implements OnInit, OnDestroy {
  public linkName: 'active' | 'active-genius' | 'active-pharmatic';
  agencySession: AgencySessionModel;
  advertiserSession: AdvertiserSessionModel;
  userSession: UserSessionModel;
  canSearchChannels: boolean;
  canViewAgentReport: boolean;
  canViewAudienceResearch: boolean;
  canRunPublisherReport: boolean;
  canRunReferrerReport: boolean;
  canViewAdvertiserEntity: boolean;
  canViewAdvertisingAccount: boolean;
  canViewAgencyAdvertiserAccount: boolean;
  canViewAdvertiserSegmentReport: boolean;
  wait: boolean;
  route$: Subscription;
  routeData: Data;
  allowedLocalSub: Subscription;

  constructor(public router: Router,
              private route: ActivatedRoute,
              private commonService: CommonService) {
    this.linkName = 'active';
    this.agencySession = new AgencySessionModel();
    this.advertiserSession = new AdvertiserSessionModel();
    this.userSession = new UserSessionModel();
    this.route$ = this.router.events
      .pipe(
        filter(event => event instanceof NavigationEnd),
        map(() => {
          const length = this.route.snapshot.children.length;
          return this.route.snapshot.children[length - 1].data;
        })
      ).subscribe(res => {
        this.routeData = res;
      });
  }

  ngOnInit(): void {
    this.wait = true;
    switch (window.location.protocol + '//' + window.location.hostname) {
      case environment.hostGenius:
        this.linkName = 'active-genius';
        break;
      case environment.hostPharmatic:
        this.linkName = 'active-pharmatic';
        break;
    }

    this.allowedLocalSub = zip(
        this.commonService.isAllowedLocal$('channel.searchInternal'),
        this.commonService.isAllowedLocal$('agentReport.view'),
        this.commonService.isAllowedLocal$('report.publisher'),
        this.commonService.isAllowedLocal$('report.referrer'),
        this.commonService.isAllowedLocal$('advertiserEntity.view'),
        this.commonService.isAllowedLocal$('account.viewAdvertising'),
        this.commonService.isAllowedLocal$('account.viewAdvertiserInAgency')
    ).subscribe(res => {
      this.canSearchChannels = res[0];
      this.canViewAgentReport = res[1];
      this.canRunPublisherReport = res[2];
      this.canRunReferrerReport = res[3];
      this.canViewAdvertiserEntity = res[4];
      this.canViewAdvertisingAccount = res[5];
      this.canViewAgencyAdvertiserAccount = res[6];
      this.wait = false;
    })
  }

  ngOnDestroy(): void {
    if (this.route$) {
      this.route$.unsubscribe();
    }
    this.allowedLocalSub.unsubscribe();
  }

  public getRouterLinkActive(): string {
    return this.linkName;
  }
}
