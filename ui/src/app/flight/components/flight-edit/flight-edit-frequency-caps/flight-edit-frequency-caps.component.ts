import {Component, Input, Output, EventEmitter} from '@angular/core';
import {FrequencyCaps} from '../../../models/flight.model';

@Component({
  selector: 'ui-flight-edit-frequency-caps',
  templateUrl: './flight-edit-frequency-caps.component.html',
  styleUrls: ['./flight-edit-frequency-caps.component.scss']
})
export class FlightEditFrequencyCapsComponent {

  @Input()
  set frequencyCaps(frequencyCaps: FrequencyCaps) {
    if (frequencyCaps) {
      this._frequencyCaps = frequencyCaps;
    } else {
      this._frequencyCaps = new FrequencyCaps();
    }
  }

  get frequencyCaps(): FrequencyCaps {
    return this._frequencyCaps;
  }

  @Input()
  set errors(errors: any) {
    if (errors.frequencyCap && errors.frequencyCap.windowLengthSpan) {
      errors.frequencyCap.windowLength = errors.frequencyCap.windowLengthSpan;
    }
    this._errors = errors;
  }

  get errors(): any {
    return this._errors;
  }

  @Output() fcChange = new EventEmitter();

  readonly timeOptions = ['SECOND', 'MINUTE', 'HOUR', 'DAY', 'WEEK'];
  private _frequencyCaps: FrequencyCaps;
  private _errors: any = {};

  constructor() {
  }

  inputChange(key: string): void {
    if (this.errors[key]) {
      delete this.errors[key];
    }

    window.setTimeout(() => {
      switch (key) {
        case 'frequencyCap.lifeCount':
          this._frequencyCaps.lifeCount = +this._frequencyCaps.lifeCount || null;
          break;
        case 'frequencyCap.windowCount':
          this._frequencyCaps.windowCount = +this._frequencyCaps.windowCount || null;
          break;
        case 'frequencyCap.windowLength':
          this._frequencyCaps.windowLength.value = +this._frequencyCaps.windowLength.value || null;
          break;
        case 'frequencyCap.period':
          this._frequencyCaps.period.value = +this._frequencyCaps.period.value || null;
          break;
      }
    });
  }
}
