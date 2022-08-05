import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {DetailedReportParametersModel} from '../../models/report.model';
import {DetailedReportService} from '../../services/detailed_report.service';
import {Observable, Subscription} from 'rxjs';
import {filter, map, switchMap, tap} from 'rxjs/operators';
import {Form, FormControl, FormGroup} from '@angular/forms';

@Component({
  selector: 'ui-detailed-report',
  templateUrl: './detailed-report.component.html',
  styleUrls: ['./detailed-report.component.scss']
})
export class DetailedReportComponent implements OnInit, OnDestroy {

  routerSubscription: Subscription;
  publisherAccountName: string;
  advertiserAccountName: string;
  reportParameters: DetailedReportParametersModel;
  accountGroup = new FormGroup({
    publisherAccount: new FormControl(),
    advertiserAccount: new FormControl(),
  });
  publisherAccounts$: Observable<any>;
  advertiserAccounts$: Observable<any>;
  meta$: Observable<any>;

  get publisherAccountCtrl(): FormControl {
    return this.accountGroup.get('publisherAccount') as FormControl;
  }

  get advertiserAccountCtrl(): FormControl {
    return this.accountGroup.get('advertiserAccount') as FormControl;
  }

  constructor(public reportService: DetailedReportService,
              private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.reportParameters = new DetailedReportParametersModel();

    this.routerSubscription = this.route.paramMap.subscribe(() => {
      this.publisherAccounts$ = this.reportService.getPublisherAccounts().pipe(
        filter(accounts => !!(accounts && accounts.length)),
        tap((accounts) => window.setTimeout(() => {
          const account = {id: null, name: ''};
          accounts.unshift(account);
          this.publisherAccountName = account.name;
          this.reportParameters.publisherAccountId = account.id;
          this.publisherAccountCtrl.setValue(account);
        }))
      );

      this.advertiserAccounts$ = this.reportService.getAdvertiserAccounts().pipe(
        filter(accounts => !!(accounts && accounts.length)),
        tap((accounts) => window.setTimeout(() => {
          const account = {id: null, name: ''};
          accounts.unshift(account);
          this.advertiserAccountName = account.name;
          this.reportParameters.advertiserAccountId = account.id;
          this.advertiserAccountCtrl.setValue(account);
        }))
      );

      this.meta$ = this.accountGroup.valueChanges.pipe(
        map(value => {
          if (value.publisherAccount) {
            this.publisherAccountName = value.publisherAccount.name;
            this.reportParameters.publisherAccountId = value.publisherAccount.id;
          }

          if (value.advertiserAccount) {
            this.advertiserAccountName = value.advertiserAccount.name;
            this.reportParameters.advertiserAccountId = value.advertiserAccount.id;
          }

          return this.reportParameters;
        }),
        switchMap(value => this.reportService.getReportMeta(value))
      );
    });
  }

  ngOnDestroy(): void {
    if (this.routerSubscription) {
      this.routerSubscription.unsubscribe();
    }
  }
}
