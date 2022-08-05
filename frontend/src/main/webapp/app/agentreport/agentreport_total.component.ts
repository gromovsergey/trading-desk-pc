import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { RouterModule }                             from '@angular/router';
import { DomSanitizer }                             from '@angular/platform-browser';

import { LoadingComponent }       from '../shared/loading.component';
import { PageComponent }          from '../shared/page.component';
import { L10nMonths, L10nReportStatuses } from '../common/L10n.const';
import { AgentReportService }     from './agentreport.service';

@Component({
    selector: 'ui-agent-report',
    templateUrl: 'total.html'
})

export class AgentReportTotalComponent extends PageComponent implements OnInit {
    @ViewChild('downloadBtn') downloadBtnEl: ElementRef;

    private totalStats: Array<any>;

    public waitStats: boolean;
    private waitDl: number = 0;

    private downloadUrl;
    private downloadName;
    private actionErrors: Array<any>;

    public L10nMonths = L10nMonths;
    public L10nReportStatuses = L10nReportStatuses;

    constructor(protected agentReportService: AgentReportService,
                private sanitizer: DomSanitizer) {
        super();
        this.initResources();
    }

    protected initResources(): void {
        this.title = '_L10N_(mainMenu.blockName.agentReport)';
    }

    ngOnInit() {
        this.initTotalStats();
    }

    initTotalStats() {
        this.waitStats = true;
        this.agentReportService.getTotalStats()
            .then(totalStats => {
                this.totalStats = totalStats;
                this.waitStats = false;
            })
            .catch(e => {
                this.waitStats = false;
            });
    }

    private downloadFile(e: any, year: number, month: number, idx : number) {
        e.preventDefault();
        this.waitDl = idx;

        this.agentReportService.downloadMonthlyStatsFile(year, month)
            .then(res => {
                this.downloadName = '_L10N_(agentReport.monthly.title)' + ' ' + L10nMonths[month] + ' ' + year + '.pdf';

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

                this.waitDl = 0;
            })
            .catch(e => {
                this.actionErrors = e['actionError'];
                this.waitDl = 0;
            });
    }
}
