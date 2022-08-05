import { Component, OnInit } from '@angular/core';
import { Router }            from '@angular/router';

import { UserSessionModel }  from '../user/user_session.model';
import { CommonService }     from './common.service';

@Component({
    template: ''
})

export class DefaultPathComponent implements OnInit {
    constructor(private router: Router,
                private commonService: CommonService) {
    }

    ngOnInit() {
        let navigation;
        let userSession = new UserSessionModel();
        if (userSession.role === 'INTERNAL') {
            this.router.navigateByUrl('/agency/dashboard');
        } else if (userSession.role === 'PUBLISHER') {
            this.router.navigateByUrl('/report/publisher/' + userSession.accountId);
        } else {
            this.commonService.isAllowedLocal0('agentReport.view')
                .then(function (canViewAgentReport) {
                    let navigation = '';
                    if (canViewAgentReport) {
                        navigation = '/agentreport/total';
                    } else if (userSession.role === 'AGENCY') {
                        navigation = '/agency/' + userSession.accountId + '/advertisers';
                    } else if (userSession.role === 'ADVERTISER') {
                        navigation = '/advertiser/' + userSession.accountId + '/flights';
                    }
                    this.router.navigateByUrl(navigation);
                }.bind(this));
        }
    }
}