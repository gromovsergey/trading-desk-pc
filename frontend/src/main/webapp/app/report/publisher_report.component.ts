import {PageComponent} from "../shared/page.component";
import {Component, OnInit} from "@angular/core";
import {PublisherReportParameters} from "./report";
import {ActivatedRoute} from "@angular/router";
import {PublisherReportParametersModel, ReportMetaModel} from "./report.model";
import {Subscription} from "rxjs/Subscription";
import {PublisherReportService} from "./publisher_report.service";

@Component({
    selector: 'ui-publisher-report',
    templateUrl: 'publisher_report.html'
})

export class PublisherReportComponent extends PageComponent implements OnInit {
    public title: string;
    public wait: boolean = true;
    private routerSubscription: Subscription;
    private accounts: Array<any>;
    private accountName: string;
    private meta: ReportMetaModel;
    private reportParameters: PublisherReportParameters;

    constructor(private reportService: PublisherReportService,
                private route: ActivatedRoute) {
        super();
        this.initResources();
    }

    protected initResources(): void {
        this.title = '_L10N_(report.publisherReport)';
    }

    ngOnInit() {
        this.reportParameters = new PublisherReportParametersModel();
        this.routerSubscription = this.route.params.subscribe(params => {
            var id = +params['id'];
            if (id) {
                this.reportService.getPublisherAccount(id).then(account => {
                    this.reportParameters.accountId = id;
                    this.accountName = account.name;
                    this.title = this.title + ': ' + this.accountName;

                    this.reportService.getReportMeta(this.reportParameters).then(meta => {
                        this.meta = meta;
                        // this.reportParameters.selectedColumns = this.meta.defaults;
                        this.wait = false;
                    });
                });
            } else {
                this.reportService.getPublisherAccounts().then(accounts => {
                    this.accounts = accounts;
                    if (this.accounts.length > 0) {
                        this.reportParameters.accountId = this.accounts[0].id;
                        this.accountName = this.accounts[0].name;
                    }

                    this.reportService.getReportMeta(this.reportParameters).then(meta => {
                        this.meta = meta;
                        // this.reportParameters.selectedColumns = this.meta.defaults;
                        this.wait = false;
                    });
                });
            }
        });
    }

    ngOnDestroy(){
        if (this.routerSubscription){
            this.routerSubscription.unsubscribe();
        }
    }

    private onAccountChange(accountId: any) {
        this.accountName = this.accounts.find(account => {
            return account.id == accountId
        }).name;
    }
}
