import {
  Component,
  Input,
  Output,
  EventEmitter,
  Type,
  ChangeDetectionStrategy,
  NgZone,
  ChangeDetectorRef
} from '@angular/core';
import {DropdownButtonMenuItem} from '../../../shared/components/dropdown-button/dropdown-button.component';
import {LineItemService} from '../../../lineitem/services/lineitem.service';
import {L10nStatic} from '../../../shared/static/l10n.static';
import {AdvertiserSessionModel} from '../../../advertiser/models';
import {MatTableDataSource} from '@angular/material/table';
import {SelectionModel} from '@angular/cdk/collections';
import {MatDialog} from "@angular/material/dialog";
import {SiteTargetingEditComponent} from "./site-targeting-edit/site-targeting-edit.component";
import {GeoTargetingEditComponent} from "./geo-targeting-edit/geo-targeting-edit.component";
import {BehaviorSubject} from "rxjs";
import {DeviceEditComponent} from "./device-edit/device-edit.component";
import {RatesEditComponent} from "./rates-edit/rates-edit.component";
import {BidStrategyEditComponent} from "./bid-strategy-edit/bid-strategy-edit.component";

export interface Action {
  component: Type<any>;
  initializer: (instance: any) => void;
}

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'ui-flight-lineitems',
  templateUrl: './flight-lineitems.component.html',
  styleUrls: ['./flight-lineitems.component.scss']
})
export class FlightLineItemsComponent {

  @Input()
  set flightId(flightId: number) {
    this._flightId = flightId;
    this.loadLocalPermissions().then();
    this.loadLineItems().then();
  }

  get flightId(): number {
    return this._flightId;
  }

  @Input() canCreate: boolean;
  @Output() liLoad: EventEmitter<any> = new EventEmitter();
  @Output() liStatusChange = new EventEmitter();
  @Output() error = new EventEmitter();

  get displayedColumns(): string[] {
    return [...(this.canEditLineItems ? ['select'] : []), 'name', 'imps', 'clicks', 'ctr', 'totalCost', 'ecpm', 'action'];
  }

  get bulkTooltip(): string {
    return localStorage.getItem('lang') === 'ru' ?
        'Пожалуйста выберите одну из стратеги!':
        'Please select at least one strategy!';
  }

  selection = new SelectionModel<any>(true, []);
  wait: boolean;
  oneIsSelected: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  canEditLineItems = false;
  lineItems: MatTableDataSource<any>;
  bulkMenu = [
    new DropdownButtonMenuItem(L10nStatic.translate('button.activate'),
      {onclick: this.bulkStatusChange.bind(this, 'ACTIVATE')}),
    new DropdownButtonMenuItem(L10nStatic.translate('button.deactivate'),
      {onclick: this.bulkStatusChange.bind(this, 'INACTIVATE')}),
    new DropdownButtonMenuItem(L10nStatic.translate('button.delete'),
      {onclick: this.bulkStatusChange.bind(this, 'DELETE')}),
    new DropdownButtonMenuItem(L10nStatic.translate('button.site'),
        {onclick: this.siteTargeting.bind(this, 'SITE')}),
    new DropdownButtonMenuItem(L10nStatic.translate('button.geoTarget'),
        {onclick: this.geoTargeting.bind(this, 'GEOTARGET')}),
    /*
    new DropdownButtonMenuItem(L10nStatic.translate('button.device'),
        {onclick: this.deviceEdit.bind(this, 'DEVICE')}),
    new DropdownButtonMenuItem(L10nStatic.translate('button.rates'),
        {onclick: this.ratesEdit.bind(this, 'RATES')}),
    new DropdownButtonMenuItem(L10nStatic.translate('button.bidStrategy'),
        {onclick: this.bidStrategyEdit.bind(this, 'BIGSTRATEGY')})
        */
  ];
  accountCurrency = new AdvertiserSessionModel().currencyCode;
  dateRange: CustomDateRange = {
    dateStart: null,
    dateEnd: null,
    value: 'TOT'
  };

  private _flightId: number;

  constructor(
      private lineItemService: LineItemService,
      private changeDetectorRef: ChangeDetectorRef,
      private ngZone: NgZone,
      public dialog: MatDialog) {
  }

  async loadLocalPermissions(): Promise<any> {
    this.canEditLineItems = false;
    try {
      this.canEditLineItems = !!(await this.lineItemService.isAllowedLocal(this.flightId, 'flight.update'));
    } catch (err) {
      console.error(err);
    }
  }

  async loadLineItems(): Promise<any> {
    this.wait = true;
    this.lineItems = null;
    try {
      const lineItems = await this.lineItemService.getListByFlightId(
        this.flightId,
        this.dateRange ? this.dateRange.dateStart : null,
        this.dateRange ? this.dateRange.dateEnd : null
      );
      this.lineItems = new MatTableDataSource(lineItems);
      this.liLoad.emit(lineItems);
    } catch (err) {
      console.error(err);
    } finally {
      this.wait = false;
    }
  }

  reloadLineItems(date: CustomDateRange): void {
    this.dateRange = date;
    this.loadLineItems().then();
  }

  changeStatus(lineItem: any): void {
    this.lineItemService
      .changeStatus(lineItem.id, lineItem.statusChangeOperation).catch((error) => {
      this.error.emit(error);
    })
      .then(newStatus => {
        lineItem.displayStatus = newStatus[0];
        this.liStatusChange.emit(null);
      });
  }

  isAllSelected(): boolean {
    const numSelected = this.selection.selected.length;
    const numRows = this.lineItems.data.length;
    this.oneIsSelected.next(!!this.selection.selected.length);
    return numSelected === numRows;
  }

  masterToggle(): void {
    if (this.isAllSelected()){
      this.selection.clear();
    } else {
      this.lineItems.data.forEach(row => this.selection.select(row));
    }
  }


  bulkStatusChange(status: string): void {
    const ids = this.selection.selected.map(lineItem => +lineItem.id);

    if (ids.length) {
      if (status === 'DELETE' && ids.length + 1 === this.lineItems.data.length
        && !confirm('All Flight values will be overwritten by the last Line Item values.\nContinue?')) {
        return;
      }

      this.wait = true;
      this.lineItemService.changeStatus(ids, status)
        .then(statusList => {
          ids.forEach((id, i) => {
            this.lineItems.data.find(f => f.id === id).displayStatus = statusList[i];
          });
          const filteredLineItems = this.lineItems.data.filter(v => v.displayStatus !== 'DELETED');
          this.lineItems = new MatTableDataSource(filteredLineItems);
          this.wait = false;
          this.liStatusChange.emit(this.lineItems);
        });
    }
  }

  private siteTargeting(): void {
    const ids = this.selection.selected.map(lineItem => +lineItem.id);

    const dialogRef = this.dialog.open(SiteTargetingEditComponent, {
      width: '700px',
      height: '710px',
      data: ids,
    });

    dialogRef.afterClosed().subscribe(result => {});
  }

  private geoTargeting(): void {
    const ids = this.selection.selected.map(lineItem => +lineItem.id);

    const dialogRef = this.dialog.open(GeoTargetingEditComponent, {
      width: '700px',
      height: '690px',
      data: ids
    });

    dialogRef.afterClosed().subscribe(result => {});
  }

  private deviceEdit(): void {
    const ids = this.selection.selected.map(lineItem => +lineItem.id);

    const dialogRef = this.dialog.open(DeviceEditComponent, {
      width: '700px',
      height: '690px',
      data: ids
    });

    dialogRef.afterClosed().subscribe(result => {});
  }

  private ratesEdit(): void {
    const ids = this.selection.selected.map(lineItem => +lineItem.id);

    const dialogRef = this.dialog.open(RatesEditComponent, {
      width: '600px',
      height: '280px',
      data: ids
    });

    dialogRef.afterClosed().subscribe(result => {});
  }

  private bidStrategyEdit(): void {
    const ids = this.selection.selected.map(lineItem => +lineItem.id);

    const dialogRef = this.dialog.open(BidStrategyEditComponent, {
      width: '600px',
      height: '350px',
      data: ids
    });

    dialogRef.afterClosed().subscribe(result => {});
  }

  public checkSelectedItems(): void {
    this.ngZone.runOutsideAngular(() => {
      //Give some time for selectionModel(set selected items)
      setTimeout(() => {
        this.oneIsSelected.next(!!this.selection.selected.length);
        this.changeDetectorRef.detectChanges()
      }, 400);
    });
  }
}
