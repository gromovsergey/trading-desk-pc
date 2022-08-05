import {PageComponent} from "../shared/page.component";
import {Component, OnInit} from "@angular/core";
import {ReferrerReportParameters} from "./report";
import {ActivatedRoute} from "@angular/router";
import {ReferrerReportParametersModel, ReportMetaModel} from "./report.model";
import {Subscription} from "rxjs/Subscription";
import {ReferrerReportService} from "./referrer_report.service";

@Component({
    selector: 'ui-referrer-report',
    templateUrl: 'referrer_report.html'
})

export class ReferrerReportComponent extends PageComponent implements OnInit {
    public title: string;
    public wait: boolean = true;
    private routerSubscription: Subscription;
    private accounts: Array<any>;
    private accountName: string;
    private sites: Array<any> = [];
    private tags: Array<any> = [];
    private meta: ReportMetaModel;
    private reportParameters: ReferrerReportParameters;

    constructor(private reportService: ReferrerReportService,
                private route: ActivatedRoute) {
        super();
        this.initResources();
    }

    protected initResources(): void {
        this.title = '_L10N_(report.referrerReport)';
    }

    ngOnInit() {
        this.reportParameters = new ReferrerReportParametersModel();
        this.routerSubscription = this.route.params.subscribe(params => {
            var id = +params['id'];
            if (id) {
                this.reportService.getPublisherAccount(id).then(account => {
                    this.reportParameters.accountId = id;
                    this.accountName = account.name;
                    this.title = this.title + ': ' + this.accountName;
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

    ngOnDestroy(){
        if (this.routerSubscription){
            this.routerSubscription.unsubscribe();
        }
    }

    public onAccountChange(accountId: number) {
        this.accountName = this.accounts.find(account => {
            return account.id == accountId
        }).name;

        this.initSitesAndTags(accountId);
    }

    public onSiteChange(event: any) {
        this.initTags(event.target.value);
    }

    public onTagChange(event: any) {
        this.reportParameters.tagIds = [];
        if (event.target.value > 0) {
            this.reportParameters.tagIds[0] = event.target.value;
        } else {
            this.reportParameters.tagIds = this.tags.map(v => v.id);
        }
    }

    private initSitesAndTags(accountId: number) {
        this.reportService.getSites(accountId)
            .then(list => {
                this.sites = list;
                if (this.sites.length > 0) {
                    this.initTags(this.sites[0].id)
                } else {
                    this.tags = [];
                    this.reportParameters.tagIds = [];
                }
            });
    }

    private initTags(siteId: number) {
        this.reportService.getTags(siteId)
            .then(list => {
                this.tags = list;
                this.reportParameters.tagIds = this.tags.map(v => v.id);
            })
    }
}
