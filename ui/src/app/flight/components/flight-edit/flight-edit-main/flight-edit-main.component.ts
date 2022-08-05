import {Component, Input, Output, EventEmitter} from '@angular/core';
import {moment, dateFormatShort, dateFormatParse, dateFormat} from '../../../../common/common.const';
import {dynamicBudget, FlightModel} from '../../../models/flight.model';
import {L10nFlightRateTypes} from '../../../../common/L10n.const';
import {AdvertiserSessionModel} from '../../../../advertiser/models';
import {ErrorHelperStatic} from '../../../../shared/static/error-helper.static';
import {getDate} from "../../../../shared/functions/workWithDate";

@Component({
  selector: 'ui-flight-edit-main',
  templateUrl: 'flight-edit-main.component.html',
  styleUrls: ['./flight-edit-main.component.scss']
})
export class FlightEditMainComponent {

  @Input()
  set flight(flight: FlightModel) {
    this._flight = flight;
    this.useDateEnd = !!flight.dateEnd;
  }

  get flight(): FlightModel {
    return this._flight;
  }

  get dynamicBudget(): number {
    return dynamicBudget(this.flight, this.spentBudget);
  }

  get dynamicDate(): string {
    const momentDateStart = moment(this.flight.dateStart).startOf('day');
    const momentResult = momentDateStart.diff(moment().startOf('day'), 'days') < 0 ? moment() : momentDateStart;

    return momentResult.format(dateFormatShort);
  }

  @Input() spentBudget: number;
  @Input() errors: any;
  @Input() isLineItem = false;
  @Input() resetableFields: string[] = [];
  @Output() resetField = new EventEmitter();

  advCurrency = new AdvertiserSessionModel().currencyCode;
  matcher = ErrorHelperStatic.getErrorMatcher;
  L10nFlightRateTypes = L10nFlightRateTypes;
  private _flight: FlightModel;
  private _useDateEnd: boolean;

  set useDateEnd(value: boolean) {
    this._useDateEnd = value;
    if (!value) {
      this.flight.dateEnd = null;
      this.flight.deliveryPacing = 'U';
    }
  }

  get useDateEnd(): boolean {
    return this._useDateEnd;
  }

  constructor() {
  }

  dateInputChange(e, key: string): void {

    const value = e.target.value.split('.');
    const temp = value[1]
    value[1] = value[0]
    value[0] = temp
    this.flight[key] = moment(new Date(value)).format(dateFormat);
    delete this.errors[key];
  }
  dateChange(date: string, key: string): void {
    if(!date) return
    // const value = date.split('.');
    // const temp = value[1]
    // value[1] = value[0]
    // value[0] = temp
    // console.log(value, date)
    // console.log(moment(value).format(dateFormat))
    this.flight[key] = moment(date).format(dateFormat);
    delete this.errors[key];

    if (date === '') {
      this.postUncheckDateEnd();
    }
  }

  getShort(value: string){
    return value ? value.split(' ')[0] : ''
  }
  setBidStrategy(type: string): void {
    this.flight.bidStrategy = type;
  }

  setDeliveryPacingType(type: string): void {
    this.flight.deliveryPacing = type;
    if (type !== 'F') {
      this.flight.dailyBudget = null;
    }
  }

  postUncheckDateEnd(): void {
    if (this.flight.deliveryPacing === 'D') {
      this.flight.deliveryPacing = 'U';
    }
  }

  onReset(e: any, type: string): void {
    e.preventDefault();
    this.resetField.emit(type);
  }
}
