import { Component, OnChanges, OnInit } from '@angular/core';
import { RouterModule, ActivatedRoute } from '@angular/router';

import { LoadingComponent }       from '../shared/loading.component';
import { PageComponent }          from '../shared/page.component';
import { IconComponent }          from '../shared/icon.component';
import { DisplayStatusDirective } from '../shared/display_status.directive';
import { UserSessionModel }       from '../user/user_session.model';

import { AgencyService }          from './agency.service';
import { DashboardService }       from './dashboard.service';
import { AgencyComponent }        from './agency.component';


@Component({
    selector: 'ui-agency-dashboard',
    templateUrl: 'dashboard.html'
})
export class AgencyDashboardComponent extends PageComponent implements OnInit, OnChanges {

    private tableLimit: number = 10;

    private user: UserSessionModel = new UserSessionModel();
    private dashboardStats: any;

    public wait: boolean;
    public waitStats: boolean;
    private flightsTableLimited: boolean = false;

    constructor(protected dashboardService: DashboardService,
                protected agencyService: AgencyService,
                protected route: ActivatedRoute){
        super();
        this.initResources();
    }

    protected initResources(): void {
        this.title = '_L10N_(agencyAccount.dashboard)';
    }

    ngOnInit() {
        this.initDashboardStats();
    }

    ngOnChanges() {
        this.initDashboardStats();
    }

    isInternalView(){
        return this.user.role === 'INTERNAL';
    }

    initDashboardStats() {
        this.waitStats = true;
        this.dashboardService.getDashboardStats()
            .then(dashboardStats => {
                this.dashboardStats  = dashboardStats;
                this.flightsTableLimited = dashboardStats.length > this.tableLimit;
                this.waitStats = false;
            })
            .catch(e => {
                this.waitStats = false;
            });
    }

    showFullFlightsTable(e: any){
        this.flightsTableLimited = false;
    }
}
