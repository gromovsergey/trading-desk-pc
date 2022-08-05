import {PageComponent} from "../shared/page.component";
import {Component, OnInit} from "@angular/core";
import {DetailedReportParameters} from "./report";
import {ActivatedRoute} from "@angular/router";
import {DetailedReportParametersModel, ReportMetaModel} from "./report.model";
import {Subscription} from "rxjs/Subscription";
import {DetailedReportService} from "./detailed_report.service";

@Component({
    selector: 'ui-detailed-report',
    templateUrl: 'detailed_report.html'
})

export class DetailedReportComponent extends PageComponent implements OnInit {
    public title: string;
    public wait: boolean = true;
    private routerSubscription: Subscription;
    private publisherAccounts: Array<any>;
    private publisherAccountName: string;
    private advertiserAccounts: Array<any>;
    private advertiserAccountName: string;
    private meta: ReportMetaModel;
    private reportParameters: DetailedReportParameters;

    constructor(private reportService: DetailedReportService,
                private route: ActivatedRoute) {
        super();
        this.initResources();
    }

    protected initResources(): void {
        this.title = '_L10N_(report.publisherReport)';
    }

    ngOnInit() {
        this.reportParameters = new DetailedReportParametersModel();
        this.routerSubscription = this.route.params.subscribe(params => {

            this.reportService.getPublisherAccounts().then(publisherAccounts => {
                this.publisherAccounts = publisherAccounts;
                if (this.publisherAccounts != null && this.publisherAccounts.length > 0) {
                    this.publisherAccounts.unshift({id: null, name: ""})
                }

                this.reportService.getAdvertiserAccounts().then(advertiserAccounts => {
                    this.advertiserAccounts = advertiserAccounts;
                    if (this.advertiserAccounts != null && this.advertiserAccounts.length > 0) {
                        this.advertiserAccounts.unshift({id: null, name: ""})
                    }

                    this.reportService.getReportMeta(this.reportParameters).then(meta => {
                        this.meta = meta;
                        // this.reportParameters.selectedColumns = this.meta.defaults;
                        this.wait = false;
                    });
                });
            });
        });
    }

    ngOnDestroy(){
        if (this.routerSubscription){
            this.routerSubscription.unsubscribe();
        }
    }

    private onPublisherAccountChange(accountId: any) {
        if (accountId == null) {
            this.publisherAccountName = null;
            return;
        }

        this.publisherAccountName = this.publisherAccounts.find(account => {
            return account.id == accountId
        }).name;
    }

    private onAdvertiserAccountChange(accountId: any) {
        if (accountId == null) {
            this.advertiserAccountName = null;
            return;
        }

        this.advertiserAccountName = this.advertiserAccounts.find(account => {
            return account.id == accountId
        }).name;
    }
}
