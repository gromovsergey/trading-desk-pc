import {Component, Input, EventEmitter, Output, OnInit, ViewChild, ElementRef} from '@angular/core';
import {AdvertiserService} from '../../../../advertiser/services/advertiser.service';
import {AdvertiserSessionModel} from '../../../../advertiser/models';
import {ArrayHelperStatic} from '../../../../shared/static/array-helper.static';
import {OptionTransferComponent} from "../../../../shared/components/option-transfer/option-transfer.component";

@Component({
  selector: 'ui-flight-edit-inventory',
  templateUrl: './flight-edit-inventory-source.component.html',
  styleUrls: ['./flight-edit-inventory-source.component.scss']
})
export class FlightEditInventorySourceComponent implements OnInit {

  @Input()
  set siteIds(ids: number[]) {
    this._ids = ids;
    if (!this.sites?.length) {
      this.loadSite();
    }
  }

  get siteIds(): number[] {
    return this._ids;
  }

  @ViewChild('optionTransfer', { static: false }) optionTransfer: OptionTransferComponent;
  @Output() sourceChange = new EventEmitter();

  public showTransfer = false;
  public wait = false;
  public loadingSites: boolean = false;
  public sites: IdName[];
  public selected: any[] = [];
  public sort = ArrayHelperStatic.sortByKey.bind(null, 'name');
  private _ids: number[];

  constructor(private advertiserService: AdvertiserService) {
  }

  ngOnInit(): void {}

  loadSite(): void {
    this.advertiserService.getSiteList$(new AdvertiserSessionModel().id).subscribe({
      next: (res) => {
        this.sites = res.map(site => ({
          id: site.siteId,
          name: site.name
        }));
        this.filterList();
      },
      error: (error) => {
        console.log(error);
      },
      complete: () => {
        this.loadingSites = true;
      }
    });
  }

  filterList(): void {
    if (this.sites && this.siteIds) {
      this.selected = this.sites.filter(site => this.siteIds.includes(site.id));
      if (this.selected.length) {
        this.showTransfer = true;
      }
    }
  }

  onSiteListChange(selected: any): void {
    /*
    TRL-18
    if (selected.length === this.sites.length) {
      this.showTransfer = false;
    }
     */
    this.sourceChange.emit(selected);
  }

  showTransferChange(value: boolean): void {
    this.showTransfer = value;
    if (!value) {
      this.sourceChange.emit(this.sites);
    }
  }
}
