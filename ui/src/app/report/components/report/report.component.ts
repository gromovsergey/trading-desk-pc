import {ChangeDetectionStrategy, ChangeDetectorRef, Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {DomSanitizer} from '@angular/platform-browser';
import {DropdownButtonMenuItem} from '../../../shared/components/dropdown-button/dropdown-button.component';
import {moment} from '../../../common/common.const';
import {ReportMetaModel, ReportParametersModel} from '../../models/report.model';
import {ErrorHelperStatic} from '../../../shared/static/error-helper.static';
import { BehaviorSubject } from 'rxjs/internal/BehaviorSubject';
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'ui-report',
  templateUrl: './report.component.html',
  styleUrls: ['./report.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ReportComponent implements OnInit {

  @ViewChild('downloadBtn') downloadBtnEl: ElementRef;
  @Input() reportService;

  @Input()
  set meta(meta: ReportMetaModel) {
    this._meta = meta;
    this.columnHash = this._meta.columnsInfo.reduce((acc: any, column: ReportColumn) => {
      const selected = this._meta.required.includes(column.id);

      acc[column.id] = {...column, selected};
      return acc;
    }, {});
    // this.initColumns();
  }

  @Input()
  set reportParameters(reportParameters: ReportParametersModel) {
    this._reportParameters = reportParameters;
    this.reportParameters.selectedColumns.forEach(column => {
      this.columnHash[column].selected = true;
    });
    this.dateRange = {
      dateStart: reportParameters.dateStart,
      dateEnd: reportParameters.dateEnd,
    };
    // this.initColumns();
  }

  @Input() downloadNamePrefix: string;
  @Input() canSelectColumns = true;
  @Input() dateRangeOptions = 'Y T WTD MTD QTD YTD LW LM LQ LY R';

  public settingsColumns: string[];
  public requiredSettingsColumns: string[];
  public statColumns: string[];
  public videoStatColumns: string[];
  public timeStatColumns: string[];
  public isLoading = new BehaviorSubject(false);
  public reportForm: FormGroup;
  public disabledColumns: string[] = [];
  public showVideoColumns: boolean = false;
  public showTimeColumns: boolean = false;
  public waitSubmit: boolean = false;
  public actionErrors: string[];
  public waitDl: boolean;
  public dlMenu: Array<any> = [];
  public dateRange: CustomDateRange;
  public columnHash: { [key: string]: ReportColumn };
  public formCollapsed: boolean = false;
  private _meta: ReportMetaModel;
  private _reportParameters: ReportParametersModel;

  report: any;
  errors: any = {};
  downloadUrl;
  downloadName;

  constructor(private sanitizer: DomSanitizer, private cdr: ChangeDetectorRef, private formBuilder: FormBuilder) {

    const dl = (type) => {
      this.downloadReport(type);
    };

    const menuCsv = new DropdownButtonMenuItem('CSV', {
      onclick: () => {
        dl('CSV');
        menuCsv.deactivate();
      }
    });
    const menuExcel = new DropdownButtonMenuItem('Excel', {
      onclick: () => {
        dl('EXCEL');
        menuExcel.deactivate();
      }
    });

    this.dlMenu = [
      menuCsv,
      menuExcel
    ];
  }

  ngOnInit(): void {
    this.initColumns();
  }

  get staticColumnsControl(): FormControl {
    return this.reportForm.get('staticColumnsSelected') as FormControl;
  }

  get settingsColumnsControl(): FormControl {
    return this.reportForm.get('settingsColumnsSelected') as FormControl;
  }

  get videoStatColumnsControl(): FormControl {
    return this.reportForm.get('videoStatColumnsSelected') as FormControl;
  }

  get timeStatColumnsControl(): FormControl {
    return this.reportForm.get('timeStatColumnsSelected') as FormControl;
  }

  get reportParameters(): ReportParametersModel {
    return this._reportParameters;
  }

  get reportSelectedParams(): string[] {
    const setting = this.settingsColumnsControl.value.length ? this.settingsColumnsControl.value : []
    const statColumnsSelected = this.staticColumnsControl.value.length ? this.staticColumnsControl.value : []
    const videoStatColumnsSelected = this.videoStatColumnsControl.value.length ? this.videoStatColumnsControl.value : []
    const timeStatColumnsSelected = this.timeStatColumnsControl.value.length ? this.timeStatColumnsControl.value : []
    return [
      ...setting,
      ...statColumnsSelected,
      ...(this.showVideoColumns ? videoStatColumnsSelected : []),
      ...(this.showTimeColumns ? timeStatColumnsSelected : []),
    ];
  }

  get meta(): ReportMetaModel {
    return this._meta;
  }

  private initColumns(): void {
    if (this.canSelectColumns) {
      this.settingsColumns = this.getLocationsColumns(this.meta.available, 'SETTINGS');
      this.statColumns = this.getLocationsColumns(this.meta.available, 'STATISTIC');
      this.videoStatColumns = this.getLocationsColumns(this.meta.available, 'VIDEO_STATISTIC');
      this.timeStatColumns = this.getLocationsColumns(this.meta.available, 'TIME_STATISTIC');
      this.initForm();
    }
  }

  private initForm(): void {
    this.reportForm = this.formBuilder.group({
      staticColumnsSelected:  new FormControl(this.filterSelected(this.statColumns), [Validators.required]),
      settingsColumnsSelected: new FormControl(this.filterSelected(this.settingsColumns), [Validators.required]),
      videoStatColumnsSelected: new FormControl(this.filterSelected(this.videoStatColumns)),
      timeStatColumnsSelected: new FormControl(this.filterSelected(this.timeStatColumns))
    });
    this.disabledColumns.push(...this.staticColumnsControl.value);
    this.requiredSettingsColumns = this.reportForm.value.settingsColumnsSelected;
  }

  private getReportParameters(): any {
    return {
      ...this.reportParameters,
      ...this.dateRange,
      selectedColumns: this.reportSelectedParams
    };
  }

  private filterSelected(columns: string[]): string[] {
    return columns.filter(item => this.columnHash[item].selected);
  }

  public getLocationsColumns(ids: any[], location: string): string[] {
    return ids.filter(id => this.columnHash[id].location === location).map((id) => this.columnHash[id].id);
  }

  public async onSubmit(): Promise<any> {
    if (this.waitSubmit) {
      return;
    }

    this.report = this.actionErrors = this.errors = null;
    this.waitSubmit = true;
    this.cdr.detectChanges();

    try {
      this.report = await this.reportService.generateReport(this.getReportParameters());
      this.formCollapsed = true;
    } catch (err) {
      this.waitSubmit = false;
      this.errors = ErrorHelperStatic.matchErrors(err);
    } finally {
      this.waitSubmit = false;
      this.cdr.detectChanges();
    }
  }

  public downloadReport(type): void {
    this.isLoading.next(true);
    this.waitDl = true;
    this.reportService.downloadReport(this.getReportParameters(), type)
      .then(res => {
        let ext = '';
        switch (type) {
          case 'EXCEL':
            ext = '.xlsx';
            break;
          case 'CSV':
          default:
            ext = '.csv';
        }
        this.downloadName = this.downloadNamePrefix + ' (' + moment().format('LLL') + ')' + ext;
        if (navigator && navigator.msSaveBlob) {
          // ie hack
          navigator.msSaveBlob(res.target.response, this.downloadName);
          this.isLoading.next(false);
          this.cdr.detectChanges();
        } else {
          const reader = new FileReader();
          reader.readAsDataURL(res.target.response);
          reader.addEventListener('load', () => {
            this.downloadUrl = this.sanitizer.bypassSecurityTrustUrl(reader.result.toString());
            window.setTimeout(() => {
              this.isLoading.next(false);
              this.cdr.detectChanges();
              this.downloadBtnEl.nativeElement.click();
            });
          });
        }

        this.waitDl = false;
      })
      .catch(err => {
        this.actionErrors = err.actionError;
        this.waitSubmit = false;
        this.isLoading.next(false);
        this.cdr.detectChanges();
      });
  }

  public getTooltip(columnName, disabledColumns: string[]): string {
    return this.getDisabledColumns(columnName, disabledColumns) ? 'Required field' : '';
  }

  public getDisabledColumns(column: string, disabledColumns: string[]): boolean {
    return disabledColumns.includes(column);
  }
}
