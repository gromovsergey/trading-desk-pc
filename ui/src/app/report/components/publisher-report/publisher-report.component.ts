import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {PublisherReportParametersModel, ReportParametersModel} from '../../models/report.model';
import {PublisherReportService} from '../../services/publisher_report.service';
import {Observable, Subscription} from 'rxjs';
import {filter, map, switchMap, tap} from 'rxjs/operators';
import {FormControl} from '@angular/forms';

@Component({
  selector: 'ui-publisher-report',
  templateUrl: './publisher-report.component.html',
  styleUrls: ['./publisher-report.component.scss']
})
export class PublisherReportComponent implements OnInit, OnDestroy {

  routerSubscription: Subscription;
  accountName: string;
  reportParameters: ReportParametersModel;
  modeById: boolean;
  accountCtrl = new FormControl();
  accounts$: Observable<any>;
  account$: Observable<any>;
  meta$: Observable<any>;

  constructor(public reportService: PublisherReportService,
              private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.reportParameters = new PublisherReportParametersModel();

    this.routerSubscription = this.route.paramMap.subscribe(paramMap => {
      if (paramMap.get('id')) {
        this.modeById = true;
        this.account$ = this.reportService.getPublisherAccount(+paramMap.get('id')).pipe(
          tap(res => {
            this.reportParameters.accountId = +res.id;
            this.accountName = res.name;
          }),
          tap(() => {
            this.meta$ = this.reportService.getReportMeta(this.reportParameters);
          })
        );
      } else {
        this.modeById = false;
        this.accounts$ = this.reportService.getPublisherAccounts().pipe(
          filter(accounts => !!(accounts && accounts.length)),
          tap((accounts) => window.setTimeout(() => {
            const account = accounts[0];
            this.accountName = account.name;
            this.reportParameters.accountId = account.id;
            this.accountCtrl.setValue(account);
          }))
        );

        this.meta$ = this.accountCtrl.valueChanges.pipe(
          map(value => {
            if (value) {
              this.accountName = value.name;
              this.reportParameters.accountId = value.id;
            }
            return this.reportParameters;
          }),
          switchMap(value => this.reportService.getReportMeta(value))
        );
      }
    });
  }

  ngOnDestroy(): void {
    if (this.routerSubscription) {
      this.routerSubscription.unsubscribe();
    }
  }
}
