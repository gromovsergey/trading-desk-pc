import {Component, OnChanges, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {UserSessionModel} from '../../../user/models/user-session.model';
import {AgencyService} from '../../services/agency.service';


@Component({
  selector: 'ui-agency-dashboard',
  templateUrl: './agency-dashboard.component.html',
  styleUrls: ['./agency-dashboard.component.scss']
})
export class AgencyDashboardComponent implements OnInit, OnChanges {

  tableLimit = 10;
  user: UserSessionModel = new UserSessionModel();
  dashboardStats: any;
  wait: boolean;
  waitStats: boolean;
  flightsTableLimited = false;
  displayedColumns: string[];

  constructor(protected agencyService: AgencyService,
              protected route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.displayedColumns = [...(this.isInternalView() ? ['agency'] : []), 'advertiser', 'object', 'version'];
    this.initDashboardStats();
  }

  ngOnChanges(): void {
    this.initDashboardStats();
  }

  isInternalView(): boolean {
    return this.user.role === 'INTERNAL';
  }

  initDashboardStats(): void {
    this.waitStats = true;

    this.agencyService.getDashboardStats()
      .then(dashboardStats => {
        this.dashboardStats = dashboardStats;
        this.flightsTableLimited = dashboardStats.length > this.tableLimit;
        this.waitStats = false;
      })
      .catch(e => {
        this.waitStats = false;
      });
  }

  showFullFlightsTable(e: any): void {
    this.flightsTableLimited = false;
  }
}
