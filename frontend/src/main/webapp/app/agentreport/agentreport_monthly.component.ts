import { Component, ElementRef, OnChanges, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { RouterModule, Router, ActivatedRoute }                           from '@angular/router';
import { DomSanitizer }                                                   from '@angular/platform-browser';
import { Subscription }                                                   from 'rxjs/Subscription';

import { LoadingComponent }         from '../shared/loading.component';
import { PageComponent }            from '../shared/page.component';
import { TableFilterComponent }     from '../common/table_filter.component';
import { jQuery as $ }              from '../common/common.const';
import { L10nMonths, L10nReportStatuses }   from '../common/L10n.const';
import * as util                    from '../common/utilities';
import { AgentReportService }       from './agentreport.service';

@Component({
    templateUrl: 'monthly.html'
})

export class AgentReportMonthlyComponent extends PageComponent implements OnInit, OnDestroy {
    @ViewChild('downloadBtn') downloadBtnEl: ElementRef;

    private monthlyStats: Array<any>;
    private status: any;
    private total: any;
    private month: number;
    private year: number;
    private pageHeader: string;

    private routerSubscription: Subscription;
    private backUrl: string = '/agentreport/total';

    public waitStats: boolean;
    private waitSubmit: boolean = false;
    private waitDl: boolean = false;

    private errors: any = {};
    private actionErrors: Array<any>;

    private downloadUrl;
    private downloadName;

    private canEdit: boolean;

    public L10nMonths = L10nMonths;
    public L10nReportStatuses = L10nReportStatuses;

    constructor(protected agentReportService: AgentReportService,
                private route: ActivatedRoute,
                private router: Router,
                private sanitizer: DomSanitizer) {
        super();
        this.initResources();
    }

    protected initResources(): void {
        if (!this.year || !this.month) {
            return;
        }

        this.title = '_L10N_(agentReport.monthly.title)' + ' ' + L10nMonths[this.month] + ' ' + this.year;
        this.pageHeader = this.title;
    }

    ngOnInit() {
        this.routerSubscription = this.route.params.subscribe(params => {
            this.year = +params['year'];
            this.month = +params['month'];
            this.initResources();

            this.waitStats = true;
            this.agentReportService.getMonthlyStats(this.year, this.month)
                .then(monthlyStats => {
                    this.monthlyStats = monthlyStats.stats;
                    this.status = monthlyStats.status;
                    this.total = monthlyStats.total;

                    return Promise.all([
                        this.agentReportService.isAllowedLocal0('agentReport.edit')
                    ]);
                })
                .then(res => {
                    this.canEdit = Boolean(res[0]);
                    this.waitStats = false;
                })
                .catch(e => {
                    this.waitStats = false;
                });
        });
    }

    private resizeTable(): void {
        let maxHeight = Math.max($(window).height() - 380, 100) + 'px';
        $('.ex-table-scrollable').css('max-height', maxHeight);
    }

    ngAfterViewInit() {
        util.onWindowEvent('resize.agentreport', this.resizeTable.bind(this));
    }

    ngAfterViewChecked() {
        this.resizeTable();
    }

    private downloadFile(e: any) {
        e.preventDefault();
        this.waitDl = true;

        this.agentReportService.downloadMonthlyStatsFile(this.year, this.month)
            .then(res => {
                this.downloadName = this.pageHeader + '.pdf';

                if (navigator && navigator.msSaveBlob) {
                    // ie hack
                    navigator.msSaveBlob(res.target.response, this.downloadName);
                } else {
                    let reader = new FileReader();
                    reader.readAsDataURL(res.target.response);
                    reader.addEventListener('load', (e) => {
                        this.downloadUrl = this.sanitizer.bypassSecurityTrustUrl(reader.result);
                        setImmediate(() => {
                            this.downloadBtnEl.nativeElement.click();
                        });
                    });
                }

                this.waitDl = false;
            })
            .catch(e => {
                this.actionErrors = e['actionError'];
                this.waitDl = false;
            });
    }

    private doSubmit(e: any) {
        e.preventDefault();
        if (this.waitSubmit) return;

        this.waitSubmit = true;
        this.agentReportService.saveMonthlyStats(this.year, this.month, this.monthlyStats)
            .then(res => {
                this.router.navigate([this.backUrl]);
            })
            .catch(e => {
                this.waitSubmit = false;
                this.errors = e.json();
            });
    }

    private hasError(rowIndex: number, property: string) {
        if (this.errors.rows && this.errors.rows[rowIndex] && this.errors.rows[rowIndex].hasOwnProperty(property)) {
            return true;
        } else {
            return false;
        }
    }

    private closePeriod(e: any) {
        e.preventDefault();

        if (this.waitSubmit) return;

        if (!confirm('_L10N_(agentReport.monthly.closeConfirm)')) {
            return;
        }

        this.waitSubmit = true;
        this.agentReportService.closeMonthlyStats(this.year, this.month, this.monthlyStats)
            .then(res => {
                this.router.navigate([this.backUrl]);
            })
            .catch(e => {
                this.waitSubmit = false;
                this.errors = e.json();
            });

        this.doSubmit(e);
    }

    ngOnDestroy() {
        if (this.routerSubscription) {
            this.routerSubscription.unsubscribe();
        }
        util.offWindowEvent('.agentreport');
    }
}
