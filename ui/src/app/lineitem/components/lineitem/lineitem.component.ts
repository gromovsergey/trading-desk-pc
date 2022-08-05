import {Component, OnInit, OnDestroy} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FlightService} from '../../../flight/services/flight.service';
import {AgencyService} from '../../../agency/services/agency.service';
import {AdvertiserService} from '../../../advertiser/services/advertiser.service';
import {LineItemService} from '../../services/lineitem.service';
import {Observable, Subject, Subscription, zip, of} from 'rxjs';
import {AdvertiserModel} from '../../../advertiser/models';
import {concatMap, switchMap, takeUntil} from "rxjs/operators";

@Component({
  selector: 'ui-lineitem',
  templateUrl: './lineitem.component.html',
  styleUrls: ['./lineitem.component.scss']
})
export class LineItemComponent implements OnInit, OnDestroy {

  private unsubscribe$: Subject<boolean>;
  lineItem;
  lineItems: Array<any>;
  flight: any; // FlightModel;
  agency: AgencyModel;
  advertiser: AdvertiserModel;
  wait = true;
  canCreateLineItem: boolean;
  canEditLineItem: boolean;

  constructor(private lineItemService: LineItemService,
              private flightService: FlightService,
              private advertiserService: AdvertiserService,
              private agencyService: AgencyService,
              private route: ActivatedRoute) {
    this.unsubscribe$ = new Subject();
  }

  ngOnInit(): void {
    this.route.params.pipe(takeUntil(this.unsubscribe$)).subscribe((routeParams) => {
      this.lineItemService.getById$(+routeParams.id)
          .pipe(concatMap(lineResponse => of(lineResponse)))
          .subscribe(response => {
            this.initData(response);
          });
    });
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next(true);
    this.unsubscribe$.unsubscribe();
  }

  private initData = (response): void => {
    this.lineItem = response.lineItemsView.pop();

    zip(this.flightService.getById(this.lineItem.flightId),
        this.lineItemService.isAllowedLocal(this.lineItem.flightId, 'flight.createLineItem'),
        this.lineItemService.isAllowedLocal(this.lineItem.id, 'flight.updateLineItem'))
        .pipe(switchMap(([one, two, three]) => {
          this.flight = one;
          this.canCreateLineItem = Boolean(two);
          this.canEditLineItem = Boolean(three);

          return this.advertiserService.getById(one.accountId)
        }
        )).subscribe(advertiser => {
          this.advertiser = advertiser;

          let promise: Promise<any> = this.advertiser.agencyId > 0 ? this.agencyService.getById(this.advertiser.agencyId) : Promise.resolve(null);
          promise.then(agency => {
                this.agency = agency;
                this.wait = false;
          });
    });
  };

  lineItemsOnLoad(e): void {
    this.lineItems = e;
  }

  statusChange(): void {
      zip(
          this.lineItemService.getById$(this.lineItem.id),
          this.flightService.getById$(this.flight.id)
      ).pipe(takeUntil(this.unsubscribe$)).subscribe(
          {
              next: (response) => {
                  this.lineItem = response[0].lineItemsView.pop();
                  this.flight = response[1];
              },
              error: (error) => {
                  this.errorProcessing();
              },
              complete: () => {}
          }
      );
  }

  makeCopy() {
    this.wait = true;
    this.lineItemService.copy(this.lineItem.id).then(() => {
      this.wait = false;
    });
  }

  public errorProcessing(): void {
      this.wait = true;

      setTimeout(() => {
          this.wait = false;
      });
  }
}
