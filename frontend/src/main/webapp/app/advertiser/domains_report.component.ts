import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {AgencyService} from '../agency/agency.service';

import {FileService} from "../shared/file.service";
import {AdvertiserComponent} from "./advertiser.component";
import {DomainsReportParametersModel, ReportMetaModel} from "../report/report.model";
import {DomainsReportParameters} from "../report/report";
import {AdvertiserService} from "./advertiser.service";
import {DomainsReportService} from "./domains_report.service";

@Component({
    selector: 'ui-domains-report',
    templateUrl: 'domains_report.html'
})

export class DomainsReportComponent extends AdvertiserComponent implements OnInit {

    public title: string;
    protected titlePrefix: string;

    private meta: ReportMetaModel;
    private reportParameters: DomainsReportParameters;

    constructor(private reportService: DomainsReportService,
                protected advertiserService: AdvertiserService,
                protected agencyService: AgencyService,
                protected fileService: FileService,
                protected route: ActivatedRoute) {

        super(advertiserService, agencyService, fileService, route);
    }

    protected initResources(): void {
        this.title = '_L10N_(report.domainsReport)';
        this.titlePrefix = '_L10N_(report.domainsReport)' + ': ';
    }

    ngOnInit() {
        this.onInit();
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
}
