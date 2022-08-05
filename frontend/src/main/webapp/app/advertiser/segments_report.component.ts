import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {AgencyService} from '../agency/agency.service';

import {FileService} from "../shared/file.service";
import {AdvertiserComponent} from "./advertiser.component";
import {SegmentsReportParametersModel, ReportMetaModel} from "../report/report.model";
import {SegmentsReportParameters} from "../report/report";
import {AdvertiserService} from "./advertiser.service";
import {SegmentsReportService} from "./segments_report.service";

@Component({
    selector: 'ui-segments-report',
    templateUrl: 'segments_report.html'
})

export class SegmentsReportComponent extends AdvertiserComponent implements OnInit {

    public title: string;
    protected titlePrefix: string;

    public meta: ReportMetaModel;
    public reportParameters: SegmentsReportParameters;

    public lineItemId: number;
    public lineItemIdNameList: Array<any>;

    constructor(private reportService: SegmentsReportService,
                protected advertiserService: AdvertiserService,
                protected agencyService: AgencyService,
                protected fileService: FileService,
                protected route: ActivatedRoute) {

        super(advertiserService, agencyService, fileService, route);
    }

    protected initResources(): void {
        this.title = '_L10N_(report.segmentsReport)';
        this.titlePrefix = '_L10N_(report.segmentsReport)' + ': ';
    }

    ngOnInit() {
        this.wait = true;
        this.onInit();

        this.promise = this.promise.then(() => {
            this.reportParameters = new SegmentsReportParametersModel();
            this.reportParameters.accountId = this.advertiser.id;
            this.wait = true;

            return Promise.all([
                this.reportService.getReportMeta(this.reportParameters),
                this.reportService.getListByAdvertiserId(this.advertiser.id)
            ]).then(res => {
                this.meta = res[0];
                this.lineItemIdNameList = res[1];
                if (this.lineItemIdNameList.length > 0) {
                    this.reportParameters.lineItemId = this.lineItemIdNameList[0].id;
                }
                this.wait = false;
            });
        });
    }

    public onlineItemChange(event: any) {
        this.reportParameters.lineItemId = event.target.value;
    }
}

