import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {AdvertiserService} from '../../../advertiser/services/advertiser.service';
import {AgencyService} from '../../../agency/services/agency.service';
import {FlightService} from '../../services/flight.service';
import {LineItemService} from '../../../lineitem/services/lineitem.service';
import {Subscription} from 'rxjs';
import {AdvertiserModel, AdvertiserSessionModel} from '../../../advertiser/models';
import {MatDialog} from '@angular/material/dialog';
import {FlightScanIOComponent} from '../flight-scan-io/flight-scan-io.component';
import {
  CreateFlightComponent
} from "../../../advertiser/components/advertiser-flights/create-flight/create-flight.component";
import {IFlightPart} from "../../../shared/components/panel/panel.component";
import {ShowInfoComponent} from "../../../shared/components/show-info/show-info.component";

enum FlightPart {
  GENERAL,
  BLACK_LIST,
  WHILE_LIST,
  GEO,
  DEFAULT_SETTING,
  AUDIT_SEGMENT,
  SSP,
  CREATIVE
}

@Component({
  selector: 'ui-flight',
  templateUrl: './flight.component.html',
  styleUrls: ['./flight.component.scss']
})
export class FlightComponent implements OnInit, OnDestroy {

  flight: any;
  agency: AgencyModel;
  advertiser: AdvertiserModel;
  advertiserSessionModel: AdvertiserSessionModel
  routerSubscription: Subscription;
  wait = true;
  specialChannelId: number = null;
  waitSpecialChannelLoad = false;
  lineItems: any[];
  canEditFlight: boolean;
  canChangeStatusFlight: boolean;
  canCreateLineItem: boolean;

  constructor(private flightService: FlightService,
              private lineItemService: LineItemService,
              private advertiserService: AdvertiserService,
              private agencyService: AgencyService,
              private route: ActivatedRoute,
              private dialog: MatDialog) {
    this.advertiserSessionModel = new AdvertiserSessionModel();
  }

  ngOnInit(): void {
    this.routerSubscription = this.route.params.subscribe(params => {
      this.loadFlightData(+params.id);
    });
  }

  ngOnDestroy(): void {
    if (this.routerSubscription) {
      this.routerSubscription.unsubscribe();
    }
  }

  loadFlightData(flightId: number): void {
    this.flightService.getById(flightId)
        .then(flight => {
          this.flight = flight;

          return Promise.all([
            this.advertiserService.getById(this.flight.accountId).toPromise(),
            this.flightService.isAllowedLocal(flightId, 'flight.update'),
            this.flightService.isAllowedLocal(flightId, 'flight.changeStatus'),
            this.flightService.isAllowedLocal(flightId, 'flight.createLineItem'),
          ]);
        })
        .then(res => {
          this.advertiser = res[0];
          this.canEditFlight = Boolean(res[1]);
          this.canChangeStatusFlight = Boolean(res[2]);
          this.canCreateLineItem = Boolean(res[3]);

          if (this.advertiser.agencyId) {
            this.agencyService.getById(this.advertiser.agencyId).then(agency => {
              this.agency = agency;
              this.wait = false;
            });
          } else {
            this.wait = false;
          }
        });
  }

  lineItemsOnLoad(e): void {
    this.lineItems = e;
    this.refreshSpecialChannelInfo();
  }

  refreshSpecialChannelInfo(): void {
    if (this.lineItems.length === 1) {
      this.waitSpecialChannelLoad = true;
      this.lineItemService.getById(this.lineItems[0].id)
          .then(res => {
            this.specialChannelId = res.lineItemsView[0].specialChannelId;
            this.waitSpecialChannelLoad = false;
          });
    }
  }

  refreshStatus(lineItems?: any): void {
    if (lineItems) {
      this.lineItems = lineItems;
      this.refreshSpecialChannelInfo();
    }

    Promise.all([
      this.flightService.getById(this.flight.id),
      this.flightService.isAllowedLocal(this.flight.id, 'flight.changeStatus')
    ]).catch(() => {
      this.errorProcessing();
    })
        .then(res => {
          this.flight = res[0];
          this.canChangeStatusFlight = Boolean(res[1]);
        });
  }

  showScanIo(): void {
    this.dialog.open(FlightScanIOComponent, {
      minWidth: 340,
      data: {
        flightId: this.flight.id,
        readonly: this.flight.displayStatus === 'DELETED' || !this.canEditFlight
      }
    });
  }

  makeCopy() {
    this.wait = true;
    this.flightService.copy(this.flight.id).then(() => {
      this.wait = false;
    });
  }

  private editFlight(id: number, type: string): void {
    //[routerLink]="['/flight', flight.id, 'edit']"
    const dialogRef = this.dialog.open(CreateFlightComponent, {
      width: '800px',
      height: '700px',
      data: {
        flightId: id,
        type: type
      }
    });

    dialogRef.afterClosed().subscribe(() => {
      this.refreshStatus();
      this.loadFlightData(id);
    });
  }

  public errorProcessing(): void {
    this.wait = true;

    setTimeout(() => {
      this.wait = false;
    });
  }

  public onApplyToStrategies(flightPart: IFlightPart): void {
    this.flightService.setPartAll(flightPart.flight.id, flightPart.enum, this.advertiserSessionModel.id).subscribe({
      next: () => {},
      error: (error) => {
        this.dialog.open(ShowInfoComponent, {
          data: { type: 'error', text: 'Упс... Что-то пошло не так, попробуйте позже!'},
          panelClass: 'custom-dialog-container',
          position: { top: '200px' },
        });
      },
      complete: () => {
        this.dialog.open(ShowInfoComponent, {
          data: { type: 'check_circle', text: 'Успешно применено к стратегиями!'},
          panelClass: 'custom-dialog-container',
          position: { top: '200px' },
        });
      }
    });
  }
}
