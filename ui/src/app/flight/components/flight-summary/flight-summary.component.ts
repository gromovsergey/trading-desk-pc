import {Component, Input, Output, EventEmitter, ViewChild, ElementRef} from '@angular/core';
import {dateFormatShort, moment} from '../../../common/common.const';
import {L10nMajorStatuses, L10nTimeUnits} from '../../../common/L10n.const';
import {LineItemService} from '../../../lineitem/services/lineitem.service';
import {FlightService} from '../../services/flight.service';
import {L10nFlightRateTypes} from '../../../common/L10n.const';
import {dynamicBudget} from '../../models/flight.model';
import {AdvertiserSessionModel} from '../../../advertiser/models';
import {ThemePalette} from '@angular/material/core/common-behaviors/color';

@Component({
  selector: 'ui-flight-summary',
  templateUrl: './flight-summary.component.html',
  styleUrls: ['./flight-summary.component.scss']
})
export class FlightSummaryComponent {

  @ViewChild('budgetBar', {static: false})
  budgetBarEl: ElementRef;

  @Input()
  set flight(flight: any) {
    this._isFlight = true;
    this._entity = flight;
    this.loadEntity();
  }

  @Input()
  set lineItem(lineItem: any) {
    this._isFlight = false;
    this._entity = lineItem;
    this.loadEntity();
  }

  @Input()
  statusChangeable: boolean;

  @Output()
  statusChange = new EventEmitter();

  get entity(): any {
    return this._entity;
  }

  get service(): any {
    return this._isFlight ? this.flightService : this.lineItemService;
  }

  stats: any;
  currencyCode = new AdvertiserSessionModel().currencyCode;
  wait = true;
  statusWait: boolean;
  frequencyCap: any; // FrequencyCaps;
  dateStart: string;
  dateEnd: string;
  progressColor: ThemePalette = 'primary';
  progressValue: number;
  L10nTimeUnits = L10nTimeUnits;
  L10nMajorStatuses = L10nMajorStatuses;
  L10nFlightRateTypes = L10nFlightRateTypes;
  private _isFlight: boolean;
  private _entity: any; // FlightModel;

  constructor(private flightService: FlightService,
              private lineItemService: LineItemService) {
  }

  async loadEntity(): Promise<any> {
    this.frequencyCap = this.entity.frequencyCap;
    this.wait = true;

    try {
      this.dateStart = this.entity.dateStart ? moment(this.entity.dateStart).format(dateFormatShort) : null;
      this.dateEnd = this.entity.dateEnd ? moment(this.entity.dateEnd).format(dateFormatShort) : null;
      this.stats = await this.service.getStatsById(this.entity.id);

    } catch (err) {
    } finally {
      this.wait = false;

      window.setTimeout(() => {
        if (this.stats.budget) {
          this.progressValue = Math.ceil(this.stats.spentBudget / this.stats.budget * 100);
          this.progressColor = this.progressValue > 100 ? 'accent' : 'primary';
        } else {
          this.progressValue = 0;
        }
      });
    }
  }

  async changeStatus(flight: any): Promise<any> {
    this.statusWait = true;
    try {
      const newStatus = await this.service.changeStatus(flight.id, flight.statusChangeOperation);
      flight.displayStatus = (this.flight ? newStatus : newStatus[0]).split('|')[0];
      this.statusChange.emit(flight.displayStatus);
    } catch (err) {
      console.error(err);
    } finally {
      this.statusWait = false;
    }
  }

  getDynamicBudget(): number {
    return dynamicBudget(this.entity, this.stats.spentBudget);
  }
}
