import {AfterViewInit, Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {AgencyService} from '../../../agency/services/agency.service';
import {FlightService} from '../../../flight/services/flight.service';
import {AdvertiserService} from '../../services/advertiser.service';
import {FileService} from '../../../shared/services/file.service';
import {Observable, Subject} from 'rxjs';
import {filter, map, switchMap} from 'rxjs/operators';
import {AdvertiserModel, AdvertiserSessionModel} from '../../models';
import { MatDialog } from '@angular/material/dialog';
import {CreateFlightComponent} from "./create-flight/create-flight.component";


@Component({
  selector: 'ui-advertiser-flights',
  templateUrl: './advertiser-flights.component.html',
  styleUrls: ['./advertiser-flights.component.scss']
})
export class AdvertiserFlightsComponent implements OnInit, AfterViewInit {

  dateRange: CustomDateRange = {
    dateStart: null,
    dateEnd: null
  };

  advertiser$: Observable<AdvertiserModel>;
  canCreateFlight$: Observable<boolean>;
  flightList$: Observable<any[]>;
  changeDate$ = new Subject();
  hashId: number;
  readonly displayedColumns = ['object', 'imps', 'clicks', 'ctr', 'totalCost', 'ecpm'];

  get currencyCode(): string {
    return new AdvertiserSessionModel().data.currencyCode;
  }

  constructor(protected advertiserService: AdvertiserService,
              protected agencyService: AgencyService,
              protected fileService: FileService,
              protected route: ActivatedRoute,
              private flightService: FlightService,
              public dialog: MatDialog) {
  }

  ngOnInit(): void {
    const routerPipe$ = this.route.paramMap.pipe(
      map(paramMap => +paramMap.get('id')),
      filter(id => !!id)
    );
    this.advertiser$ = routerPipe$.pipe(
      switchMap(id => this.advertiserService.getById(id))
    );
    this.canCreateFlight$ = routerPipe$.pipe(
      switchMap(id => this.flightService.isAllowedLocal(id, 'flight.create')),
    );
    this.flightList$ = this.changeDate$.pipe(
      switchMap((dateRange: CustomDateRange) => {
        this.dateRange = dateRange;
        routerPipe$.subscribe(id => this.hashId = id)
        return routerPipe$;
      }),
      switchMap((id) => this.flightService.getStatListByAdvertiserId(id, this.dateRange?.dateStart, this.dateRange?.dateEnd))
    );
  }

  ngAfterViewInit(): void {
    this.changeDate$.next(this.dateRange);
  }

  public flightAdd(): void {
    //routerLink="/flight/add  FlightEditComponent"
    const dialogRef = this.dialog.open(CreateFlightComponent, {
      width: '800px',
      height: '700px',
      data: {
        flightId: null,
        type: 'add'
      }
    });

    dialogRef.afterClosed().subscribe(() => {
      this.flightList$ = this.flightService.getStatListByAdvertiserId(this.hashId, this.dateRange?.dateStart, this.dateRange?.dateEnd);
    });
  }
}
