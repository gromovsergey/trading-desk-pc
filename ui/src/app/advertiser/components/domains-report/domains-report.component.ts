import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {AgencyService} from '../../../agency/services/agency.service';
import {FileService} from '../../../shared/services/file.service';
import {AdvertiserComponent} from '../advertiser/advertiser.component';
import {DomainsReportParametersModel, ReportMetaModel} from '../../../report/models/report.model';
import {AdvertiserService} from '../../services/advertiser.service';
import {DomainsReportService} from '../../services/domains_report.service';
import {MatDialog} from '@angular/material/dialog';

@Component({
  selector: 'ui-domains-report',
  templateUrl: './domains-report.component.html',
  styleUrls: ['./domains-report.component.scss']
})
export class DomainsReportComponent extends AdvertiserComponent implements OnInit {

  title: string;
  titlePrefix: string;
  meta: ReportMetaModel;
  reportParameters: DomainsReportParameters;
  accounts: any; // @todo not defined value

  constructor(public reportService: DomainsReportService,
              protected advertiserService: AdvertiserService,
              protected agencyService: AgencyService,
              protected fileService: FileService,
              protected route: ActivatedRoute,
              protected dialog: MatDialog) {
    super(advertiserService, agencyService, fileService, route, dialog);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.promise = this.promise.then(() => {
      this.reportParameters = new DomainsReportParametersModel();
      this.reportParameters.accountId = this.advertiser.id;
      this.wait = true;
      return this.reportService.getReportMeta(this.reportParameters);
    }).then(meta => {
      this.meta = meta;
      this.wait = false;
    });
  }

  // @todo not defined behaviour
  onAccountChange(): void {
  }
}
