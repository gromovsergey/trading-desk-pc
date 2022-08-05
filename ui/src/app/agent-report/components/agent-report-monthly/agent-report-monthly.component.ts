import {ChangeDetectionStrategy, ChangeDetectorRef, Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {DomSanitizer} from '@angular/platform-browser';
import {Subscription} from 'rxjs';
import {L10nMonths, L10nReportStatuses} from '../../../common/L10n.const';
import {AgentReportService} from '../../services/agentreport.service';
import {L10nStatic} from '../../../shared/static/l10n.static';
import {MatTableDataSource} from '@angular/material/table';
import {ErrorHelperStatic} from '../../../shared/static/error-helper.static';

@Component({
  selector: 'ui-agent-report-monthly',
  templateUrl: './agent-report-monthly.component.html',
  styleUrls: ['./agent-report-monthly.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AgentReportMonthlyComponent implements OnInit, OnDestroy {

  @ViewChild('downloadBtn') downloadBtnEl: ElementRef;

  get editable(): boolean {
    return this.canEdit && this.status !== 'CLOSED';
  }

  get title(): string {
    return `${L10nStatic.translate('agentReport.monthly.title')} ${L10nStatic.translate(L10nMonths[this.month])} ${this.year}`;
  }

  monthlyStats: MatTableDataSource<any[]>;
  status: any;
  total: any;
  month: number;
  year: number;
  routerSubscription: Subscription;
  waitStats: boolean;
  waitSubmit = false;
  waitDl = false;
  errors: any = {};
  actionErrors: any[];
  downloadUrl;
  downloadName;
  canEdit: boolean;
  matcher = ErrorHelperStatic.getErrorMatcher;

  readonly displayedColumns: string[] = ['advertiser', 'contractNumber', 'client', 'campaign', 'rateType',
    'rateValue', 'inventory', 'inventoryConfirmed', 'inventoryComment', 'invoiceNumber', 'totalAmount',
    'totalAmountConfirmed', 'pubAmount', 'pubAmountConfirmed', 'pubAmountComment', 'agentAmount', 'principalAmount'];
  readonly L10nMonths = L10nMonths;
  readonly L10nReportStatuses = L10nReportStatuses;
  readonly backUrl = '/agent-report/total';

  constructor(private agentReportService: AgentReportService,
              private route: ActivatedRoute,
              private router: Router,
              private cdr: ChangeDetectorRef,
              private sanitizer: DomSanitizer) {
  }


  ngOnInit(): void {
    this.routerSubscription = this.route.params.subscribe(params => {
      this.year = +params.year;
      this.month = +params.month;
      this.loadStats(this.year, this.month);
    });
  }

  async loadStats(year: number, month: number): Promise<any> {
    this.waitStats = true;
    try {
      const res = await this.agentReportService.getMonthlyStats(year, month);
      this.monthlyStats = new MatTableDataSource(res.stats);
      this.status = res.status;
      this.total = res.total;

      this.canEdit = !!(await this.agentReportService.isAllowedLocal0('agentReport.edit'));
      this.waitStats = false;
    } catch (err) {
      console.error(err);
    } finally {
      this.waitStats = false;
      this.cdr.detectChanges();
    }
  }

  downloadFile(e: any): void {
    e.preventDefault();
    this.waitDl = true;

    this.agentReportService.downloadMonthlyStatsFile(this.year, this.month)
      .then(res => {
        this.downloadName = this.title + '.pdf';
        this.cdr.detectChanges();
        const reader = new FileReader();
        reader.readAsDataURL(res.target.response);
        reader.addEventListener('load', () => {
          this.downloadUrl = this.sanitizer.bypassSecurityTrustUrl(reader.result.toString());
          this.cdr.detectChanges();
          window.setTimeout(() => {
            this.downloadBtnEl.nativeElement.click();
            this.waitDl = false;
            this.cdr.detectChanges();
          });
        });
      })
      .catch(err => {
        this.actionErrors = err.actionError;
        this.waitDl = false;
        this.cdr.detectChanges();
      });
  }

  async doSubmit(e: any): Promise<any> {
    e.preventDefault();
    if (this.waitSubmit) {
      return;
    }

    try {
      this.waitSubmit = true;
      await this.agentReportService.saveMonthlyStats(this.year, this.month, this.monthlyStats.data);
      await this.router.navigateByUrl(this.backUrl);
    } catch (err) {
      this.errors = err.error;
    } finally {
      this.waitSubmit = false;
      this.cdr.detectChanges();
    }
  }

  hasError(rowIndex: number, property: string): boolean {
    return (this.errors.rows && this.errors.rows[rowIndex] && this.errors.rows[rowIndex].hasOwnProperty(property));
  }

  async closePeriod(e: any): Promise<any> {
    e.preventDefault();

    if (this.waitSubmit) {
      return;
    }

    if (!window.confirm(L10nStatic.translate('agentReport.monthly.closeConfirm'))) {
      return;
    }

    this.waitSubmit = true;
    try {
      await this.agentReportService.saveMonthlyStats(this.year, this.month, this.monthlyStats.data);
      await this.agentReportService.closeMonthlyStats(this.year, this.month, this.monthlyStats.data);
      await this.router.navigateByUrl(this.backUrl);
    } catch (err) {
      this.waitSubmit = false;
      this.errors = err.error;
      this.cdr.detectChanges();
    }
  }

  ngOnDestroy(): void {
    if (this.routerSubscription) {
      this.routerSubscription.unsubscribe();
    }
  }

  applyFilter(event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value;
    this.monthlyStats.filter = filterValue.trim().toLowerCase();
  }
}
