import {Component, OnChanges, Input, ViewChild} from '@angular/core';
import {DropdownButtonMenuItem} from '../../../shared/components/dropdown-button/dropdown-button.component';
import {AdvertiserService} from '../../../advertiser/services/advertiser.service';
import {LineItemService} from '../../../lineitem/services/lineitem.service';
import {FlightService} from '../../services/flight.service';
import {AdvertiserSessionModel} from '../../../advertiser/models';
import {L10nStatic} from '../../../shared/static/l10n.static';
import {MatTableDataSource} from '@angular/material/table';
import {MatPaginator} from '@angular/material/paginator';
import {SelectionModel} from '@angular/cdk/collections';
import {MatDialog} from '@angular/material/dialog';
import {FlightSitesConnectComponent} from '../flight-sites-connect/flight-sites-connect.component';
import {take} from 'rxjs/operators';

@Component({
  selector: 'ui-flight-sites',
  templateUrl: './flight-sites.component.html',
  styleUrls: ['./flight-sites.component.scss']
})
export class FlightSitesComponent implements OnChanges {

  @Input() flightId: number;
  @Input() lineItemId: number;
  @Input() short: boolean;
  @Input() readonly = false;
  @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;

  wait = true;
  sites: MatTableDataSource<any>;
  bulkMenu = [];
  errors = {
    unchekedAll: false,
    popupUnchekedAll: false
  };
  service;
  entityId: number;

  selection = new SelectionModel<any>(true, []);

  get displayedColumns(): string[] {
    return [...(!this.readonly ? ['select'] : []), 'name',
      ...(!this.short ? ['imps', 'clicks', 'ctr'] : []), 'uniqueUsers', 'action'];
  }

  constructor(private flightService: FlightService,
              private lineItemService: LineItemService,
              private advertiserService: AdvertiserService,
              private dialog: MatDialog) {
    this.bulkMenu.push(
      new DropdownButtonMenuItem(L10nStatic.translate('button.unlink'), {onclick: this.deleteSites.bind(this)})
    );
  }

  ngOnChanges(): void {
    this.service = this.flightId ? this.flightService : this.lineItemService;
    this.entityId = this.flightId ? this.flightId : this.lineItemId;
    this.loadSitesList();
  }

  loadSitesList(): void {
    if (this.entityId) {
      this.wait = true;
      this.service
        .getSiteList(this.entityId)
        .then(list => {
          this.selection.clear();
          this.sites = new MatTableDataSource<any>(list);
          this.sites.paginator = this.paginator;
          this.wait = false;
        });
    }
  }

  showPopup(): void {
    const advertiser = new AdvertiserSessionModel();
    this.dialog.open(FlightSitesConnectComponent, {
      minWidth: 360,
      data: {
        advertiserId: advertiser.id,
        siteIds: this.sites.data.map(item => item.siteId)
      }
    }).afterClosed().pipe(take(1)).subscribe(async (res: number[]) => {
      if (res && res.length) {
        this.wait = true;
        try {
          await this.service.linkSites(this.entityId, res);
          await this.loadSitesList();
        } catch (err) {
          console.error(err);
        } finally {
          this.wait = false;
        }
      }
    });
  }

  deleteSites(siteId?: number): void {
    const selectedIds = siteId ? [siteId] : this.selection.selected.map(item => item.siteId);
    const linkedIds = this.sites.data.map(item => item.siteId).filter(id => !selectedIds.includes(id));

    this.wait = true;
    this.service.linkSites(this.entityId, linkedIds)
      .then(() => {
        this.loadSitesList();
      });
  }

  isAllSelected(): boolean {
    const numSelected = this.selection.selected.length;
    const numRows = this.sites.data.length;
    return numSelected === numRows;
  }

  masterToggle(): void {
    if (this.isAllSelected()) {
      this.selection.clear();
    } else {
      this.sites.data.forEach(row => this.selection.select(row));
    }
  }
}
