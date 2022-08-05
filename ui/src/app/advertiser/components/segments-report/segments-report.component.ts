import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {AgencyService} from '../../../agency/services/agency.service';
import {FileService} from '../../../shared/services/file.service';
import {SegmentsReportParametersModel, ReportMetaModel} from '../../../report/models/report.model';
import {AdvertiserService} from '../../services/advertiser.service';
import {FlightService} from '../../../flight/services/flight.service';
import {SegmentsReportService} from '../../services/segments_report.service';
import {LineItemService} from '../../../lineitem/services/lineitem.service';
import {ConversionService} from '../../../conversion/services/conversion.service';
import {AdvertiserComponent} from '../advertiser/advertiser.component';
import {MatDialog} from '@angular/material/dialog';

@Component({
  selector: 'ui-segments-report',
  templateUrl: './segments-report.component.html',
  styleUrls: ['./segments-report.component.scss']
})
export class SegmentsReportComponent extends AdvertiserComponent implements OnInit {

  title: string;
  titlePrefix: string;
  meta: ReportMetaModel;
  reportParameters: SegmentsReportParameters;
  flights: any[];
  flightRadio = false;
  flightId: number;
  flightsAvailable = [];
  flightsSelected = [];
  lineItemRadio = false;
  lineItems: any[];
  lineItemsAvailable = [];
  lineItemsSelected = [];

  constructor(public reportService: SegmentsReportService,
              protected advertiserService: AdvertiserService,
              protected flightService: FlightService,
              protected lineItemService: LineItemService,
              protected segmentService: ConversionService,
              protected agencyService: AgencyService,
              protected fileService: FileService,
              protected route: ActivatedRoute,
              protected dialog: MatDialog) {

    super(advertiserService, agencyService, fileService, route, dialog);
  }

  ngOnInit(): void {
    this.wait = true;
    super.ngOnInit();

    this.promise = this.promise.then(() => {
      this.wait = true;

      this.reportParameters = new SegmentsReportParametersModel();
      this.reportParameters.accountId = this.advertiser.id;

      return Promise.all([
        this.reportService.getReportMeta(this.reportParameters),
        this.flightService.getListByAdvertiserId(this.advertiser.id)
      ]).then(res => {
        this.meta = res[0];
        this.flights = res[1];
        this.initFlightReportParameters();
        this.initFlightsOptiontransfer();
        this.wait = false;
      });
    });
  }

  switchFlightSelectType(): void {
    this.flightRadio = !this.flightRadio;
    if (this.flightRadio) {
      this.initFlightsOptiontransfer();
    } else {
      this.initFlightReportParameters();
    }
  }

  onFlightChange(flightId: number): void {
    this.lineItems = null;
    this.resetLineItems();

    this.reportParameters.flightIds = [];
    if (flightId > 0) {
      this.reportParameters.flightIds[0] = flightId;
      this.flightId = flightId;
      this.initLineItems();
    } else {
      this.initFlightReportParameters();
    }
  }

  onFlightListChange(e: any): void {
    const ids = [];
    for (const v of e) {
      ids.push(v.id);
    }
    this.reportParameters.flightIds = ids;

    this.resetLineItems();
    this.flightId = null;
    if (ids.length === 1) {
      this.reportParameters.flightIds[0] = ids[0];
      this.flightId = ids[0];
      this.initLineItems();
    }
  }

  initFlightReportParameters(): void {
    if (this.flights
          && this.flights.length > 0) {
      this.reportParameters.flightIds = this.flights.map(f => f.id);
      this.resetLineItems();
      this.flightId = null;
    } else {
      this.unselectFlightReportParameters();
    }
  }

  unselectFlightReportParameters(): void {
    this.reportParameters.flightIds = [];
    this.flightId = null;
  }

  initFlightsOptiontransfer(): void {
    this.flightsAvailable = [];
    this.flightsSelected = [];

    this.flights.forEach(v => {
      this.flightsAvailable.push({
        id: v.id,
        name: v.name
      });

      // ToDo: uncomment this:
      // if (this.reportParameters.flightIds.indexOf(v.id) !== -1) {
      //   this.flightsSelected.push({
      //     id: v.id,
      //     name: v.name
      //   });
      // } else {
      //   this.flightsAvailable.push({
      //     id: v.id,
      //     name: v.name
      //   });
      // }
    });
  }

  switchLineItemType(): void {
    this.lineItemRadio = !this.lineItemRadio;
    if (!this.lineItemRadio) {
      this.resetLineItems();
    } else {
      this.initLineItems();
    }
  }

  initLineItems(): any {
    this.reportParameters.lineItemIds = [];
    if (!this.lineItems) {
      return this.lineItemService.getListByFlightId(this.flightId, null, null)
        .then(list => {
          this.lineItems = list;
          this.initLineItemsOptiontransfer();
        });
    } else {
      this.initLineItemsOptiontransfer();
    }
  }

  initLineItemsOptiontransfer(): void {
    this.lineItemsAvailable = [];
    this.lineItemsSelected = [];

    this.lineItems.forEach(v => {
      if (this.reportParameters.lineItemIds.indexOf(v.id) !== -1) {
        this.lineItemsSelected.push({
          id: v.id,
          name: v.name
        });
      } else {
        this.lineItemsAvailable.push({
          id: v.id,
          name: v.name
        });
      }
    });
  }

  resetLineItems(): void {
    this.reportParameters.lineItemIds = [];
    this.lineItemsAvailable = [];
    this.lineItemsSelected = [];
  }

  onLineItemsChange(e: any): void {
    const ids = [];
    for (const v of e) {
      ids.push(v.id);
    }
    this.reportParameters.lineItemIds = ids;
    this.reportParameters.flightIds = [];
  }

  sort(a, b): number {
    if (a.name === b.name) {
      return 0;
    }
    return (a.name > b.name) ? 1 : -1;
  }
}
