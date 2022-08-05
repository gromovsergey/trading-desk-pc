import { Component, Input, OnChanges, Output, EventEmitter} from '@angular/core';
import { RouterModule}                                      from '@angular/router';
import { FormsModule}                                       from '@angular/forms';

import { IconComponent }                                        from '../shared/icon.component';
import { HintComponent }                                        from '../shared/hint.component';
import { DatetimeDirective }                                    from '../shared/datetime.directive';
import { moment, dateFormatShort } from '../common/common.const';
import { AdvertiserSessionModel }                               from '../advertiser/advertiser_session.model';
import { FlightModel, dynamicBudget }                           from "./flight.model";
import {L10nFlightRateTypes} from '../common/L10n.const';

@Component({
    selector: 'ui-flight-edit-main',
    templateUrl: 'main.html'
})

export class FlightEditMainComponent implements OnChanges {

    @Input() flight;
    @Input() spentBudget: number;
    @Input() errors: any;
    @Input() isLineItem: boolean = false;
    @Input() resetableFields: Array<string> = [];
    @Output() resetField    = new EventEmitter();

    public advCurrency  = new AdvertiserSessionModel().currencyCode;
    private _dateEnd: string;

    public L10nFlightRateTypes = L10nFlightRateTypes;

    constructor(){
    }

    ngOnChanges(){
        this._dateEnd = this.flight.dateEnd;
    }

    public dateChange(date: string, key: string){
        if (typeof date === 'string'){
            this.flight[key]    = date;
            delete this.errors[key];

            if (date === null || date === '') {
                this.postUncheckDateEnd();
            }
        }
    }

    private setBidStrategy(type: string){
        this.flight.bidStrategy = type;
    }

    public toggleRateType(e: any){
        e.preventDefault();
        this.flight.rateType = this.flight.rateType === 'CPC' ? 'CPM' : 'CPC';
    }

    public setDeliveryPacingType(type: string){
        this.flight.deliveryPacing  = type;
        if (type !== 'F') {
            this.flight.dailyBudget = null;
        }
    }

    public toggleDateEnd(e: any){
        if (e.target.checked){
            if (moment(this._dateEnd).isAfter(this.flight.dateStart)){
                this.flight.dateEnd = this._dateEnd;
            } else {
                this.flight.dateEnd = this._dateEnd = moment().format(dateFormatShort);
            }
        } else {
            this._dateEnd   = this.flight.dateEnd;
            this.flight.dateEnd = '';

            this.postUncheckDateEnd();
        }
    }

    private postUncheckDateEnd() {
        if (this.flight.deliveryPacing === 'D'){
            this.flight.deliveryPacing = 'U';
        }
    }

    public getDynamicBudget(): number {
        return dynamicBudget(this.flight, this.spentBudget);
    }

    private getDynamicDate(): string {
        let momentDateStart = moment(this.flight.dateStart).startOf('day'),
            momentResult    = momentDateStart.diff(moment().startOf('day'), 'days') < 0 ? moment() : momentDateStart;

        return momentResult.format(dateFormatShort);
    }

    private onReset(e: any, type: string){
        e.preventDefault();
        this.resetField.emit(type);
    }
}
