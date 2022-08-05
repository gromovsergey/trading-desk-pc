import {Component, ContentChild, ElementRef, Input, OnInit, ViewChild} from "@angular/core";
import {DomSanitizer} from "@angular/platform-browser";
import {DropdownButtonMenuItem} from "../shared/dropdown_button.component";
import {moment} from "../common/common.const";
import {ReportService} from "./report";
import {ReportMetaModel, ReportParametersModel} from "./report.model";
import {DateRange} from "../shared/date_range";
import {DateRangeModel} from "../shared/date_range.model";

@Component({
    selector: 'ui-report',
    templateUrl: 'report.html'
})

export class ReportComponent implements OnInit {

    @ViewChild('btnSubmit') btnSubmitEl: ElementRef;
    @ViewChild('downloadBtn') downloadBtnEl: ElementRef;
    @ViewChild('datestart') dateStartEl: ElementRef;
    @ViewChild('dateend') dateEndEl: ElementRef;
    @ViewChild('daterange') dateRangeEl: ElementRef;

    @ContentChild('params') bodyContentEl: ElementRef;
    @ViewChild('params') bodyChildEl: ElementRef;

    @Input() meta: ReportMetaModel;
    @Input() reportService: ReportService;
    @Input() reportParameters: ReportParametersModel;
    @Input() downloadNamePrefix: string;
    @Input() canSelectColumns: boolean = true;
    @Input() dateRangeOptions: string = "Y T WTD MTD QTD YTD LW LM LQ LY R";

    public settingsCollapsed: boolean = false;

    public settingsColumns: Array<string>;
    public statColumns: Array<string>;

    public videoStatColumns: Array<string>;
    public showVideoColumns: boolean = false;

    public timeStatColumns: Array<string>;
    public showTimeColumns: boolean = false;

    public waitSubmit: boolean = false;
    public report: Array<any>;
    public actionErrors: Array<any>;
    public errors: any = {};
    public waitDl: boolean;

    public dlMenu = [];
    public downloadUrl;
    public downloadName;

    public dateRange: DateRange = new DateRangeModel();


    constructor(private sanitizer: DomSanitizer) {

        let dl = (type) => {
                this.downloadReport(type);
            },
            menuCsv = new DropdownButtonMenuItem('CSV', {
                onclick: function () {
                    dl('CSV');
                    menuCsv.deactivate();
                }
            }),
            menuExcel = new DropdownButtonMenuItem('Excel', {
                onclick: function () {
                    dl('EXCEL');
                    menuExcel.deactivate();
                }
            });

        this.dlMenu = [
            menuCsv,
            menuExcel
        ];
    }

    ngOnInit(){
        this.reportParameters.selectedColumns = this.meta.defaults;
        if (this.canSelectColumns) {
            this.settingsColumns = this.getLocationsColumns(this.meta.available, 'SETTINGS');
            this.statColumns = this.getLocationsColumns(this.meta.available, 'STATISTIC');
            this.videoStatColumns = this.getLocationsColumns(this.meta.available, 'VIDEO_STATISTIC');
            this.timeStatColumns = this.getLocationsColumns(this.meta.available, 'TIME_STATISTIC');
        }

        this.bodyChildEl.nativeElement.appendChild(this.bodyContentEl.nativeElement);
    }

    private getLocationsColumns(ids: Array<any>, location: string) {
        return this.meta.columnsInfo
            .filter(f => ids.includes(f.id) && f.location === location)
            .map(f => {
                return f.id;
            });
    }

    private getColumnName(id: string) {
        let found = this.meta.columnsInfo.find(f => {
            return f.id === id
        });
        return found ? found.name : null;
    }

    private isActive(item) {
        return this.reportParameters.selectedColumns.find(v => {
            return v === item
        }) !== undefined;
    }

    private toggleColumn(e: any, item: string) {
        e.preventDefault();

        if (this.meta.required.includes(item) && this.isActive(item)) {
            return;
        }

        if (this.isActive(item)) {
            this.reportParameters.selectedColumns = this.reportParameters.selectedColumns.filter(f => {
                return f !== item
            });
        } else {
            this.reportParameters.selectedColumns.push(item);
        }

        // sort
        let sorted = [];
        this.meta.available.forEach(v => {
            if (this.isActive(v)) sorted.push(v);
        });
        this.reportParameters.selectedColumns = sorted;
    }

    public toggleVideoColumns(e: any){
        if (!e.target.checked){
            this.showVideoColumns = false;
            this.reportParameters.selectedColumns = this.reportParameters.selectedColumns.filter(v => {
                return !this.videoStatColumns.includes(v);
            });
        } else {
            this.showVideoColumns = true;
        }
    }

    public toggleTimeColumns(e: any){
        if (!e.target.checked){
            this.showTimeColumns = false;
            this.reportParameters.selectedColumns = this.reportParameters.selectedColumns.filter(v => {
                return !this.timeStatColumns.includes(v);
            });
        } else {
            this.showTimeColumns = true;
        }
    }

    public onSubmit(e: any) {
        if (this.waitSubmit || this.btnSubmitEl.nativeElement.disabled) return;

        this.prepareReportParameters();

        this.report = null;
        this.actionErrors = null;
        this.errors = null;
        this.settingsCollapsed = true;
        this.waitSubmit = true;

        this.reportService.generateReport(this.reportParameters)
            .then(report => {
                this.report = report;
                this.waitSubmit = false;
            })
            .catch(e => {
                this.actionErrors = e.json()['actionError'];
                this.errors = e.json();
                this.waitSubmit = false;
            });
    }

    private downloadReport(type) {
        this.waitDl = true;

        this.prepareReportParameters();

        this.reportService.downloadReport(this.reportParameters, type)
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
                this.waitSubmit = false;
            });
    }

    public settingsToggle(e: boolean) {
        this.settingsCollapsed = e;
    }

    private prepareReportParameters() {
        this.reportParameters.dateStart = this.dateRange.dateStart;
        this.reportParameters.dateEnd = this.dateRange.dateEnd;
    }
}
