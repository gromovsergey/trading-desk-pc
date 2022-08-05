import {Component, OnChanges, Input, ViewChild, ElementRef, Output, EventEmitter} from '@angular/core';
import {DropdownButtonMenuItem} from '../../../shared/components/dropdown-button/dropdown-button.component';
import {CreativeService} from '../../../creative/services/creative.service';
import {LineItemService} from '../../../lineitem/services/lineitem.service';
import {FlightService} from '../../services/flight.service';
import {AdvertiserSessionModel} from '../../../advertiser/models';
import {SelectionModel} from '@angular/cdk/collections';
import {MatPaginator} from '@angular/material/paginator';
import {MatTableDataSource} from '@angular/material/table';
import {L10nStatic} from '../../../shared/static/l10n.static';
import {MatDialog} from '@angular/material/dialog';
import {CreativePreviewComponent} from '../../../creative/components/creative-preview/creative-preview.component';
import {take} from 'rxjs/operators';
import {FlightCreativesConnectComponent} from '../flight-creatives-connect/flight-creatives-connect.component';


@Component({
  selector: 'ui-flight-creatives',
  templateUrl: 'flight-creatives.component.html',
  styleUrls: ['./flight-creatives.component.scss']
})
export class FlightCreativesComponent implements OnChanges {
  @Input() flightId: number;
  @Input() lineItemId: number;
  @Input() short: boolean;
  @Input() readonly = false;
  @Output() statusChange = new EventEmitter();
  @Output() error = new EventEmitter();
  @ViewChild('checkAll') checkAllInput: ElementRef;
  @ViewChild('checkAllLink') checkAllLinkInput: ElementRef;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  wait = true;
  canUpdate: boolean;
  advertiser: AdvertiserSessionModel = new AdvertiserSessionModel();
  creatives: MatTableDataSource<any>;
  selection = new SelectionModel<any>(true, []);
  bulkMenu = [];
  private service;
  private entityId: number;

  get displayedColumns(): string[] {
    return [...(!this.readonly ? ['select'] : []), 'name', 'size', 'template',
      ...(!this.short ? ['imps', 'clicks', 'ctr'] : []), 'uniqueUsers', 'action'];
  }

  constructor(private flightService: FlightService,
              private lineItemService: LineItemService,
              private creativeService: CreativeService,
              private dialog: MatDialog) {
    this.bulkMenu.push(
      new DropdownButtonMenuItem(L10nStatic.translate('button.unlink'), {onclick: this.deleteCreatives.bind(this)})
    );
  }

  ngOnChanges(): void {
    this.service = this.flightId ? this.flightService : this.lineItemService;
    this.entityId = this.flightId ? this.flightId : this.lineItemId;
    this.loadCreativeList();
  }

  loadCreativeList(): void {
    this.wait = true;
    if (this.entityId) {
      Promise.all([
        this.service.getCreativeList(this.entityId),
        this.flightId ?
          this.service.isAllowedLocal(this.entityId, 'flight.updateFlightCreatives') :
          this.service.isAllowedLocal(this.entityId, 'flight.updateLineItemCreatives')
      ]).then(res => {
        this.canUpdate = res[1];
        this.creatives = new MatTableDataSource<any>(res[0]);
        this.wait = false;
        window.setTimeout(() => {
          this.creatives.paginator = this.paginator;
        });
      });
    } else {
      this.wait = false;
    }
  }

  showPopup(): void {
    this.dialog.open(FlightCreativesConnectComponent, {
      minWidth: 360,
      data: {
        advertiserId: this.advertiser.id,
        creativeIds: this.creatives.data.map(item => item.creativeId)
      },
    }).afterClosed().pipe(take(1)).subscribe(async (res: number[]) => {
      if (res && res.length) {
        this.wait = true;
        try {
          const newStatus = await this.service.linkCreatives(this.entityId, res);
          this.statusChange.emit(newStatus);
          await this.loadCreativeList();
        } catch (err) {
          console.error(err);
        } finally {
          this.wait = false;
        }
      }
    });
  }

  changeStatus(creative: any): void {
    this.service
      .linkStatusChange(this.entityId, creative.creativeId, creative.statusChangeOperation).catch((error) => {
        this.error.emit(error);
    })
      .then(newStatus => {
        creative.displayStatus = newStatus.creativeLinkDispayStatus.split('|')[0];
        this.statusChange.emit(newStatus);
      });
  }

  deleteCreatives(creativeId?: number): void {
    const selectedIds = creativeId ? [creativeId] : this.selection.selected.map(item => item.creativeId);
    const linkedIds = this.creatives.data.map(item => item.creativeId).filter(id => !selectedIds.includes(id));

    this.wait = true;
    this.service.linkCreatives(this.entityId, linkedIds)
      .then(newStatus => {
        this.statusChange.emit(newStatus);
        this.loadCreativeList();
      });
  }

  preview(creative: any): void {
    this.dialog.open(CreativePreviewComponent, {
      data: {creative},
    });
  }

  isAllSelected(): boolean {
    const numSelected = this.selection.selected.length;
    const numRows = this.creatives.data.length;
    return numSelected === numRows;
  }

  masterToggle(): void {
    if (this.isAllSelected()) {
      this.selection.clear();
    } else {
      this.creatives.data.forEach(row => this.selection.select(row));
    }
  }
}
