import { Component, Input, OnChanges, Output, EventEmitter, ViewChild, ElementRef } from '@angular/core';
import { CurrencyPipe }                                                             from '@angular/common';
import { Router }                                                                   from '@angular/router';

import { LoadingComponent }             from '../shared/loading.component';
import { IconComponent }                from '../shared/icon.component';
import { CellComponent }                from '../shared/cell.component';
import { DisplayStatusToggleComponent } from '../shared/display_status_toggle.component';
import { DisplayStatusDirective }       from '../shared/display_status.directive';
import { dateFormatShort, moment }           from '../common/common.const';
import { L10nMajorStatuses, L10nTimeUnits }     from '../common/L10n.const';
import { AdvertiserSessionModel }       from '../advertiser/advertiser_session.model';
import { LineItemService }              from '../lineitem/lineitem.service';
import { FlightService }                from './flight.service';
import { FlightModel, FrequencyCaps, dynamicBudget} from './flight.model';
import {L10nFlightRateTypes} from '../common/L10n.const';

@Component({
    selector: 'ui-flight-summary',
    templateUrl: 'summary.html'
})

export class FlightSummaryComponent implements OnChanges {

    @ViewChild('budgetBar') budgetBarEl: ElementRef;
    @Input() flight: FlightModel;
    @Input() lineItem: FlightModel;
    @Input() statusChangeable: boolean;
    @Output() onStatusChange    = new EventEmitter();

    private stats;
    private currencyCode    = new AdvertiserSessionModel().currencyCode;
    public wait: boolean   = true;
    private statusWait: boolean;

    private entity: FlightModel;
    private frequencyCap: FrequencyCaps;

    private dateStart: string;
    private dateEnd: string;

    public L10nTimeUnits = L10nTimeUnits;
    public L10nMajorStatuses = L10nMajorStatuses;
    public L10nFlightRateTypes = L10nFlightRateTypes;

    constructor(private flightService: FlightService,
                private lineItemService: LineItemService,
                private router: Router){
    }

    ngOnChanges(){
        this.entity  = this.flight ? this.flight : this.lineItem;
        let service = this.flight ? this.flightService : this.lineItemService;

        this.frequencyCap  = this.flight ? this.flight.frequencyCap : this.lineItem.frequencyCap;

        service.getStatsById(this.entity.id)
            .then(stats => {
                this.dateStart  = this.entity.dateStart ? moment(this.entity.dateStart).format(dateFormatShort) : null;
                this.dateEnd    = this.entity.dateEnd ? moment(this.entity.dateEnd).format(dateFormatShort) : null;

                this.stats      = stats;
                this.wait       = false;

                setImmediate(()=>{
                    if (this.stats.budget) {
                        this.budgetBarEl.nativeElement.style.width  = Math.ceil(this.stats.spentBudget / this.stats.budget * 100)+'%';
                    } else {
                        this.budgetBarEl.nativeElement.style.width  = '0px';
                    }
                });
            });
    }

    private changeStatus(flight: any){
        let service = this.flight ? this.flightService : this.lineItemService;

        this.statusWait = true;
        service.changeStatus(flight.id, flight.statusChangeOperation)
            .then(newStatus => {
                flight.displayStatus = (this.flight ? newStatus : newStatus[0]).split('|')[0];
                this.onStatusChange.emit(flight.displayStatus);
                this.statusWait = false;
            });
    }

    public getDynamicBudget(): number {
        return dynamicBudget(this.entity, this.stats.spentBudget);
    }
}
