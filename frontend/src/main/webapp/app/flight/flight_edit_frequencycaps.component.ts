import { Component, Input, Output, EventEmitter } from '@angular/core';
import { RouterModule }                           from '@angular/router';
import { FormsModule }                            from '@angular/forms';

import { IconComponent } from '../shared/icon.component';
import { FrequencyCaps } from './flight.model';

@Component({
    selector: 'ui-flight-edit-fc',
    templateUrl: 'frequency_caps.html'
})

export class FlightEditFrequencyCapsComponent {

    @Input() fc;
    @Input() errors;

    @Output() onChange  = new EventEmitter();

    public _fc: FrequencyCaps;

    constructor() {
    }

    ngOnChanges(){
        if (this.fc !== null) {
            this._fc = Object.assign({}, this.fc);
        } else {
            this._fc = new FrequencyCaps();
        }

        if (this.errors.frequencyCap && this.errors.frequencyCap.windowLengthSpan){
            this.errors.frequencyCap.windowLength = this.errors.frequencyCap.windowLengthSpan;
        }
    }

    public inputChange(key: string){
        if (this.errors[key]){
            delete this.errors[key];
        }

        setImmediate(() => {
            switch (key){
                case 'frequencyCap.lifeCount':
                    this.fc.lifeCount  = this._fc.lifeCount = +this._fc.lifeCount || null;
                    break;
                case 'frequencyCap.windowCount':
                    this.fc.windowCount  = this._fc.windowCount = +this._fc.windowCount || null;
                    break;
                case 'frequencyCap.windowLength':
                    this.fc.windowLength.value  = +this._fc.windowLength.value || null;
                    break;
                case 'frequencyCap.period':
                    this.fc.period.value  = +this._fc.period.value || null;
                    break;
            }
        });
    }
}
