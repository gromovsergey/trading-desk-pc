import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {DomSanitizer} from '@angular/platform-browser';
import {L10nMonths, L10nReportStatuses} from '../../../common/L10n.const';
import {AgentReportService} from '../../services/agentreport.service';
import {Observable} from 'rxjs';
import {L10nStatic} from '../../../shared/static/l10n.static';

@Component({
  selector: 'ui-agent-report',
  templateUrl: './agent-report-total.component.html',
  styleUrls: ['./agent-report-total.component.scss']
})
export class AgentReportTotalComponent implements OnInit {
  @ViewChild('downloadBtn', {static: false}) downloadBtnEl: ElementRef;

  downloadUrl;
  downloadName;
  actionErrors: Array<any>;
  L10nMonths = L10nMonths;
  L10nReportStatuses = L10nReportStatuses;

  totalStats$: Observable<any>;
  readonly displayedColumns = [
    'month', 'invoiceAmount', 'publisherAmount', 'agencyAmount', 'principalAmount', 'status', 'download'
  ];

  constructor(protected agentReportService: AgentReportService,
              private sanitizer: DomSanitizer) {
  }


  ngOnInit(): void {
    this.totalStats$ = this.agentReportService.getTotalStats();
  }

  downloadFile(e: MouseEvent, year: number, month: number): void {
    const el = (e.target as HTMLButtonElement);
    el.disabled = true;

    this.agentReportService.downloadMonthlyStatsFile(year, month)
      .then(res => {
        this.downloadName = [
          L10nStatic.translate('agentReport.monthly.title'),
          L10nStatic.translate(L10nMonths[month]),
          `${year}.pdf`
        ].join(' ');

        const reader = new FileReader();
        reader.readAsDataURL(res.target.response);
        reader.addEventListener('load', () => {
          this.downloadUrl = this.sanitizer.bypassSecurityTrustUrl(reader.result.toString());
          window.setTimeout(() => {
            this.downloadBtnEl.nativeElement.click();
            el.disabled = false;
          });
        });
      })
      .catch(err => {
        this.actionErrors = err.actionError;
      });
  }
}
