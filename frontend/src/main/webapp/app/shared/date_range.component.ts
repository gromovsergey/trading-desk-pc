import {Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild} from "@angular/core";
import {dateFormatParse, dateFormatShort, moment} from "../common/common.const";
import {DateRange} from "./date_range";
import {L10nDateRanges} from '../common/L10n.const';

@Component({
    selector: 'ui-date-range',
    templateUrl: 'date_range.html'
})

export class DateRangeComponent implements OnInit {

    @ViewChild('datestart') dateStartEl: ElementRef;
    @ViewChild('dateend') dateEndEl: ElementRef;
    @ViewChild('daterange') dateRangeEl: ElementRef;

    @Input() dateRange: DateRange;
    @Input() options: string;
    @Input() defaultValue: string;

    @Output() onChange: EventEmitter<any>  = new EventEmitter();

    public optionsList: Array<String>;
    public tmpToday: string = moment().format(dateFormatParse);

    public L10nDateRanges = L10nDateRanges;

    ngOnInit() {
        this.optionsList = this.options.split(' ');
        this.onDateRangeChange({target: {value: this.defaultValue}});
    }

    public dateChange(e: string, key: string) {
        this.dateRange[key] = e;
        this.dateRangeEl.nativeElement.value = 'R';
        this.onChange.emit(e);
    }

    public onDateRangeChange(event?: any) {
        let s, e;
        switch (event.target.value) {
            case 'TOT':
                s = null;
                e = null;
                break;
            case 'Y':
                s = moment().subtract(1, 'day').startOf('day').format(dateFormatParse);
                e = moment().subtract(1, 'day').endOf('day').format(dateFormatParse);
                break;
            case 'T':
                s = moment().startOf('day').format(dateFormatParse);
                e = moment().format(dateFormatParse);
                break;
            case 'WTD':
                s = moment().startOf('week').format(dateFormatParse);
                e = moment().format(dateFormatParse);
                break;
            case 'MTD':
                s = moment().startOf('month').format(dateFormatParse);
                e = moment().format(dateFormatParse);
                break;
            case 'QTD':
                s = moment().startOf('quarter').format(dateFormatParse);
                e = moment().format(dateFormatParse);
                break;
            case 'YTD':
                s = moment().startOf('year').format(dateFormatParse);
                e = moment().format(dateFormatParse);
                break;
            case 'LW':
                s = moment().subtract(1, 'week').startOf('week').format(dateFormatParse);
                e = moment().subtract(1, 'week').endOf('week').format(dateFormatParse);
                break;
            case 'LM':
                s = moment().subtract(1, 'month').startOf('month').format(dateFormatParse);
                e = moment().subtract(1, 'month').endOf('month').format(dateFormatParse);
                break;
            case 'LQ':
                s = moment().subtract(1, 'quarter').startOf('quarter').format(dateFormatParse);
                e = moment().subtract(1, 'quarter').endOf('quarter').format(dateFormatParse);
                break;
            case 'LY':
                s = moment().subtract(1, 'year').startOf('year').format(dateFormatParse);
                e = moment().subtract(1, 'year').endOf('year').format(dateFormatParse);
                break;
            default:
                return;
        }

        this.dateRange.dateStart = s;
        this.dateRange.dateEnd = e;

        if (this.optionsList.includes('R')) {
            setImmediate(() => {
                this.dateStartEl.nativeElement.value = moment(s, dateFormatParse).format(dateFormatShort);
                this.dateEndEl.nativeElement.value = moment(e, dateFormatParse).format(dateFormatShort);
            });
        }

        this.onChange.emit(event);
    }
}
