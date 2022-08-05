import { Component }            from '@angular/core';
import { Router } from '@angular/router';

import { AgencySessionModel }     from '../agency/agency_session.model';
import { AdvertiserSessionModel } from '../advertiser/advertiser_session.model';
import { UserSessionModel }       from '../user/user_session.model';
import { CommonService }          from './common.service';


@Component({
    selector: 'ui-main',
    templateUrl: 'menu_main.html'
})

export class MenuMainComponent {

    private agencySession: AgencySessionModel;
    private advertiserSession: AdvertiserSessionModel;
    private userSession: UserSessionModel;

    private canSearchChannels: boolean;
    private canViewAgentReport: boolean;
    private canViewAudienceResearch: boolean;
    private canRunPublisherReport: boolean;
    private canRunReferrerReport: boolean;
    private canViewAdvertiserEntity: boolean;
    private canViewAdvertisingAccount: boolean;
    private canViewAgencyAdvertiserAccount: boolean;
    public wait:boolean;
    private promise: Promise<any>;

    public constructor(private router: Router,
                       private commonService: CommonService){
        this.agencySession      = new AgencySessionModel();
        this.advertiserSession  = new AdvertiserSessionModel();
        this.userSession        = new UserSessionModel();
    }

    ngOnInit() {
        this.wait = true;

        this.promise = Promise.all([
            this.commonService.isAllowedLocal0('channel.search'),
            this.commonService.isAllowedLocal0('agentReport.view'),
            this.commonService.isAllowedLocal0('audienceResearch.view'),
            this.commonService.isAllowedLocal0('report.publisher'),
            this.commonService.isAllowedLocal0('report.referrer'),
            this.commonService.isAllowedLocal0('advertiserEntity.view'),
            this.commonService.isAllowedLocal0('account.viewAdvertising'),
            this.commonService.isAllowedLocal0('account.viewAdvertiserInAgency')
        ]).then(res => {
            this.canSearchChannels = res[0];
            this.canViewAgentReport = res[1];
            this.canViewAudienceResearch = res[2];
            this.canRunPublisherReport = res[3];
            this.canRunReferrerReport = res[4];
            this.canViewAdvertiserEntity = res[5];
            this.canViewAdvertisingAccount = res[6];
            this.canViewAgencyAdvertiserAccount = res[7];

            this.wait = false;
        });
    }
}
