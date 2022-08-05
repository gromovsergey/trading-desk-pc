import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {ReferrerReportParametersModel, ReportMetaModel} from '../../models/report.model';
import {Subscription} from 'rxjs';
import {ReferrerReportService} from '../../services/referrer_report.service';

@Component({
  selector: 'ui-referrer-report',
  templateUrl: './referrer-report.component.html',
  styleUrls: ['./referrer-report.component.scss']
})
export class ReferrerReportComponent implements OnInit, OnDestroy {
  wait = true;
  routerSubscription: Subscription;
  accounts: any[];
  accountName: string;
  sites = [];
  tags = [];
  meta: ReportMetaModel;
  reportParameters: ReferrerReportParameters;
  siteId: number;
  tagId: number;

  constructor(public reportService: ReferrerReportService,
              private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.reportParameters = new ReferrerReportParametersModel();
    this.routerSubscription = this.route.params.subscribe(params => {
      const id = +params.id;

      if (id) {
        this.reportService.getPublisherAccount(id).then(account => {
          this.reportParameters.accountId = id;
          this.accountName = account.name;
          this.initSitesAndTags(this.reportParameters.accountId);

          this.reportService.getReportMeta(this.reportParameters).then(meta => {
            this.meta = meta;
            this.reportParameters.selectedColumns = this.meta.defaults;
            this.wait = false;
          });
        });
      } else {
        this.reportService.getPublisherAccounts().then(accounts => {
          this.accounts = accounts;
          if (this.accounts.length > 0) {
            this.reportParameters.accountId = this.accounts[0].id;
            this.accountName = this.accounts[0].name;
            this.initSitesAndTags(this.reportParameters.accountId);
          } else {
            this.sites = [];
            this.tags = [];
            this.reportParameters.tagIds = [];
          }
          this.reportService.getReportMeta(this.reportParameters).then(meta => {

            this.meta = meta;
            this.reportParameters.selectedColumns = this.meta.defaults;
            this.wait = false;
          });
        });
      }
    });
  }

  ngOnDestroy(): void {
    if (this.routerSubscription) {
      this.routerSubscription.unsubscribe();
    }
  }

  onAccountChange(accountId: number): void {
    this.accountName = this.accounts.find(account => account.id === accountId).name;

    this.initSitesAndTags(accountId);
  }

  onSiteChange(): void {
    this.initTags(this.siteId);
  }

  onTagChange(): void {
    this.reportParameters.tagIds = [];
    if (this.tagId > 0) {
      this.reportParameters.tagIds[0] = this.tagId;
    } else {
      this.reportParameters.tagIds = this.tags.map(v => v.id);
    }
  }

  initSitesAndTags(accountId: number): void {
    this.reportService.getSites(accountId)
      .then(list => {
        this.sites = list;
        if (this.sites.length > 0) {
          this.initTags(this.sites[0].id);
        } else {
          this.tags = [];
          this.reportParameters.tagIds = [];
        }
      });
  }

  initTags(siteId: number): void {
    this.reportService.getTags(siteId)
      .then(list => {
        this.tags = list;
        this.reportParameters.tagIds = this.tags.map(v => v.id);
      });
  }
}
