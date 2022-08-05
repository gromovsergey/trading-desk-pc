import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {dateFormatParse, moment} from '../../../common/common.const';
import {L10nDateRanges} from '../../../common/L10n.const';
import {FormControl} from '@angular/forms';
import {DateAdapter} from '@angular/material/core';
import {LS_LANG} from '../../../const';
import {getDate} from "../../functions/workWithDate";

@Component({
  selector: 'ui-date-range',
  templateUrl: 'date-range.component.html',
  styleUrls: ['./date-range.component.scss']
})
export class DateRangeComponent implements OnInit {

  @Input()
  set dateRange(dateRange: CustomDateRange) {
    if (!dateRange) {
      return;
    }
    this.dateStart.patchValue(new Date(dateRange.dateStart));
    this.dateEnd.patchValue(new Date(dateRange.dateEnd));
    this._dateRange = dateRange;
  }

  get dateRange(): CustomDateRange {
    return this._dateRange;
  }

  @Input() options: string;
  @Input() defaultValue: string;
  @Output() dateRangeChange: EventEmitter<CustomDateRange> = new EventEmitter();

  optionsList: string[];
  L10nDateRanges = L10nDateRanges;
  optionSelector = new FormControl('');
  dateStart = new FormControl('');
  dateEnd = new FormControl('');
  private _dateRange: CustomDateRange;

  constructor(private _adapter: DateAdapter<any>) {
  }

  ngOnInit(): void {
    const locale = window.localStorage.getItem(LS_LANG);
    if (locale) {
      this._adapter.setLocale(locale);
    }

    this.optionsList = this.options.split(' ');
    if (this.optionsList.length) {
      this.optionSelector.patchValue(this.optionsList[0]);
    }
  }

  dateRangeChangeHandler(value: string): void {
    let dateStart;
    let dateEnd;

    switch (value) {
      case 'TOT':
        dateStart = null;
        dateEnd = null;
        break;
      case 'Y':
        dateStart = moment().subtract(1, 'day').startOf('day').format(dateFormatParse);
        dateEnd = moment().subtract(1, 'day').endOf('day').format(dateFormatParse);
        break;
      case 'T':
        dateStart = moment().startOf('day').format(dateFormatParse);
        dateEnd = moment().format(dateFormatParse);
        break;
      case 'WTD':
        dateStart = moment().startOf('week').format(dateFormatParse);
        dateEnd = moment().format(dateFormatParse);
        break;
      case 'MTD':
        dateStart = moment().startOf('month').format(dateFormatParse);
        dateEnd = moment().format(dateFormatParse);
        break;
      case 'QTD':
        dateStart = moment().startOf('quarter').format(dateFormatParse);
        dateEnd = moment().format(dateFormatParse);
        break;
      case 'YTD':
        dateStart = moment().startOf('year').format(dateFormatParse);
        dateEnd = moment().format(dateFormatParse);
        break;
      case 'LW':
        dateStart = moment().subtract(1, 'week').startOf('week').format(dateFormatParse);
        dateEnd = moment().subtract(1, 'week').endOf('week').format(dateFormatParse);
        break;
      case 'LM':
        dateStart = moment().subtract(1, 'month').startOf('month').format(dateFormatParse);
        dateEnd = moment().subtract(1, 'month').endOf('month').format(dateFormatParse);
        break;
      case 'LQ':
        dateStart = moment().subtract(1, 'quarter').startOf('quarter').format(dateFormatParse);
        dateEnd = moment().subtract(1, 'quarter').endOf('quarter').format(dateFormatParse);
        break;
      case 'LY':
        dateStart = moment().subtract(1, 'year').startOf('year').format(dateFormatParse);
        dateEnd = moment().subtract(1, 'year').endOf('year').format(dateFormatParse);
        break;
      case 'R':
        break;
      default:
        return;
    }
    this.dateRangeChange.emit({dateStart, dateEnd, value});
  }

  changeDateRange(e?, type?){
    this.optionSelector.patchValue('R')
    let dateStart;
    let dateEnd;
    if(type === 'dateStart'){
      const value = e.target.value.split('.');
      const temp = value[1]
      value[1] = value[0]
      value[0] = temp
      dateStart = getDate(new Date(value))
      dateEnd = getDate(this.dateEnd.value)
    } else if(type === 'dateEnd'){
      const value = e.target.value.split('.');
      const temp = value[1]
      value[1] = value[0]
      value[0] = temp
      dateEnd = getDate(new Date(value))
      dateStart = getDate(this.dateStart.value)
    }else{
      dateStart = getDate(this.dateStart.value)
      dateEnd = getDate(this.dateEnd.value)
    }

    this.dateRangeChange.emit({dateStart, dateEnd, value: 'R'});
  }

}
