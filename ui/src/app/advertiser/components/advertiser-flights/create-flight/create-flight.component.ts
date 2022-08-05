import {Component, Inject, Input, OnDestroy, OnInit} from '@angular/core';
import {LineItemService} from "../../../../lineitem/services/lineitem.service";
import {FlightService} from "../../../../flight/services/flight.service";
import {AdvertiserSessionModel} from "../../../models";
import {ErrorHelperStatic} from "../../../../shared/static/error-helper.static";
import {FlightModel, FrequencyCaps} from "../../../../flight/models/flight.model";
import { L10nTimeZones } from 'src/app/common/L10n.const';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {Subscription, zip} from "rxjs";
import {AdvertiserService} from "../../../services/advertiser.service";

interface DialogData {
  flightId: number;
  type: string;
}

@Component({
  selector: 'ui-create-flight',
  templateUrl: './create-flight.component.html',
  styleUrls: ['./create-flight.component.scss']
})
export class CreateFlightComponent implements OnInit, OnDestroy {

  @Input() isLineItem = false;
  public strings: {lineitem: any, flight: any};
  public mode: string;
  public resetableFields: string[] = null;
  public advertiserSession: AdvertiserSessionModel;
  public errors: any = {};
  public waitSubmit = false;
  public flight = new FlightModel();
  public wait: boolean = true;
  private flightDefaults: Flight = new FlightModel();
  public spentBudget: number = 0;
  public L10nTimeZones = L10nTimeZones;
  public entityId: number;
  public sub = new Subscription();

  constructor(
      private lineItemService: LineItemService,
      private flightService: FlightService,
      public dialogRef: MatDialogRef<CreateFlightComponent>,
      @Inject(MAT_DIALOG_DATA) public dialogData: DialogData,
      private advertiserService: AdvertiserService,
  ) {
    this.advertiserSession = new AdvertiserSessionModel();
    this.mode = '';
    this.resetableFields = [];
  }

  ngOnInit(): void {
    this.mode = this.dialogData.type;
    this.entityId = this.dialogData.flightId;

    if (this.mode === 'edit') {
      this.sub.add(zip(this.flightService.getById$(this.entityId), this.flightService.getStatsById$(this.entityId))
          .subscribe(response => {
            let entity = response[0];
            this.spentBudget = response[1].spentBudget;

            if (entity.frequencyCap === null) {
              entity.frequencyCap = new FrequencyCaps();
            }

            this.flight = entity as any;

            new AdvertiserSessionModel().hasData() ?
                this.wait = false:
                this.getAdvertiserSession();
          }));
    }

    this.wait = false;
  }

  ngOnDestroy(): void {
    this.sub && this.sub.unsubscribe();
  }

  private getAdvertiserSession(): void {
    this.sub.add(this.advertiserService.getById(this.flight.accountId)
        .subscribe(advertiser => {
          new AdvertiserSessionModel().data = advertiser;
          this.wait = false;
        }));
  }

  submitForm(): void {
    const entity = Object.assign({}, this.flight);
    const service = this.flightService;

    entity.emptyProps = [];
    this.waitSubmit = true;

    Object.keys(this.flight).forEach(v => {
      if (!['resetAwareProps', 'propsWithFlightValues'].includes(v)) {
        if (this.flight[v] === '' || this.flight[v] === null || (Array.isArray(this.flight[v]) && this.flight[v].length === 0)) {
          entity.emptyProps.push(v);
        }
      }
    });

    this.mode === 'add' ?
        this.createFlight(service, entity):
        this.editFlight(service, entity);
  }

  private createFlight(service, entity): void {
    entity.accountId = this.advertiserSession.id;
    service.save$(entity).subscribe({
      next: (res) => {
        this.waitSubmit = false;
        this.dialogRef.close();
      },
      error: (error) => {
        if (error.status === 412) {
          this.errors = ErrorHelperStatic.matchErrors(error);
        }
        this.waitSubmit = false;
      },
      complete: () => {}
    });
  }

  private editFlight(service, entity): void {
    entity.accountId = this.advertiserSession.id;
    service.update$(entity).subscribe({
      next: (res) => {
        this.waitSubmit = false;
        this.dialogRef.close();
      },
      error: (error) => {
        if (error.status === 412) {
          this.errors = ErrorHelperStatic.matchErrors(error);
        }
        this.waitSubmit = false;
      },
      complete: () => {}
    });
  }

  geoChange(e: any): void {
    this.flight.geoChannelIds = e;
  }

  excludedGeoChange(e: any): void {
    this.flight.excludedGeoChannelIds = e;
  }

  geoAddressesChange(e: any): void {
    this.flight.addresses = e;
  }

  excludedGeoAddressesChange(e: any): void {
    this.flight.excludedAddresses = e;
  }

  onDScheduleChange(e: any): void {
    this.flight.schedules = e;
  }

  convTrackingChange(e: any): void {
    this.flight.conversionIds = e;
  }

  siteIdsChange(list: IdName[]): void {
    this.flight.siteIds = list.map(item => item.id);
  }

  devicesChange(e: any): void {
    this.flight.deviceChannelIds = e;
  }

  resetField(e: any, fieldName: string): void {
    if (e) {
      e.preventDefault();
      e.stopPropagation();
    }

    if (!this.isLineItem) {
      return;
    }

    if (this.flight.propsWithFlightValues === undefined) {
      this.flight.propsWithFlightValues = [];
    }

    const fieldNames: Array<string> = [fieldName];
    switch (fieldName) {
      case 'dateEnd':
        if (this.flightDefaults.dateEnd === null && this.flight.deliveryPacing === 'D') {
          fieldNames.push('deliveryPacing', 'dailyBudget');
        }
        break;
      case 'deliveryPacing':
        fieldNames.push('dailyBudget');
        if (this.flightDefaults.deliveryPacing === 'D' && this.flight.dateEnd === null) {
          fieldNames.push('dateEnd');
        }
        break;
      case 'rateType':
        fieldNames.push('rateValue');
        break;
      case 'geoChannelIds':
        fieldNames.push('geoChannelIds', 'excludedGeoChannelIds');
        break;
    }

    const obj: any = {};
    fieldNames.forEach(f => {
      obj[f] = this.flightDefaults[f];
      this.flight.propsWithFlightValues.push(f);
    });
    if (fieldName === 'frequencyCap' && obj.frequencyCap !== null) {
      delete obj.frequencyCap.id;
    }
    this.flight = Object.assign({}, this.flight, obj);
    this.resetableFields = this.resetableFields.filter(f => !fieldNames.includes(f));
  }

  public close(): void {
    this.dialogRef.close();
  }
}
