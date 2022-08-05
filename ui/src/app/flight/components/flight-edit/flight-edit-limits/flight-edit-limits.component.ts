import {Component, Input, Output, EventEmitter} from '@angular/core';
import {FlightModel} from '../../../models/flight.model';
import {ErrorHelperStatic} from "../../../../shared/static/error-helper.static";
import {AdvertiserSessionModel} from "../../../../advertiser/models";
import {dateFormatShort, moment} from "../../../../common/common.const";

@Component({
  selector: 'ui-flight-edit-limits',
  templateUrl: './flight-edit-limits.component.html',
  styleUrls: ['./flight-edit-limits.component.scss']
})
export class FlightEditLimitsComponent {

  @Input()
  set flight(flight: FlightModel) {
    this._flight = flight;
  }

  get flight(): FlightModel {
    return this._flight;
  }

  get dynamicDate(): string {
    const momentDateStart = moment(this.flight.dateStart).startOf('day');
    const momentResult = momentDateStart.diff(moment().startOf('day'), 'days') < 0 ? moment() : momentDateStart;

    return momentResult.format(dateFormatShort);
  }

  get diffDate(): string {
    const momentDateStart = moment(this.flight.dateStart);
    const momentDateEnd = moment(this.flight.dateEnd);

    return momentDateEnd.diff(momentDateStart,'days');
  }
  get impressionsTotalLimitAuto(): number {

    return Math.ceil(this.flight.impressionsTotalLimit / Number.parseInt(this.diffDate))
  }

  get clicksTotalLimitAuto(): number {

    return Math.ceil(this.flight.clicksTotalLimit / Number.parseInt(this.diffDate))
  }

  @Input()
  set errors(errors: any) {
    // if (errors.limits && errors.frequencyCap.windowLengthSpan) {
    //   errors.frequencyCap.windowLength = errors.frequencyCap.windowLengthSpan;
    // }
    this._errors = errors;
  }

  get errors(): any {
    return this._errors;
  }

  @Input() resetableFields: string[] = [];
  @Output() fcChange = new EventEmitter();
  @Output() resetField = new EventEmitter();

  readonly timeOptions = ['SECOND', 'MINUTE', 'HOUR', 'DAY', 'WEEK'];
  private _flight: FlightModel;
  private _errors: any = {};
  advCurrency = new AdvertiserSessionModel().currencyCode;
  matcher = ErrorHelperStatic.getErrorMatcher;



  constructor() {
  }

  inputChange(key: string): void {
    if (this.errors[key]) {
      delete this.errors[key];
    }

    window.setTimeout(() => {
      switch (key) {
        case 'minCtrGoal':
          if(this.flight.minCtrGoal < 0){
            this.flight.minCtrGoal = 0
          }
          if( this.flight.minCtrGoal > 100){
            this.flight.minCtrGoal = Number(this.flight.minCtrGoal.toString().slice(0, -1))
            this.errors[key] = ['error']
          }
          break;
        case 'limits.impressionsTotalLimit':
          if(this.flight.impressionsTotalLimit < 0){
            this.flight.impressionsTotalLimit = 0
          }
          this.flight.impressionsTotalLimit = Math.floor(this.flight.impressionsTotalLimit)
          break;
        case 'limits.clicksTotalLimit':
          if(this.flight.clicksTotalLimit < 0){
            this.flight.clicksTotalLimit = 0
          }
          this.flight.clicksTotalLimit = Math.floor(this.flight.clicksTotalLimit)
          break;
      }
    });
  }

  onReset(e: any, type: string): void {
    e.preventDefault();
    this.resetField.emit(type);
  }
}
