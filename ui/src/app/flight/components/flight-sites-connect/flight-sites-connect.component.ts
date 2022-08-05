import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {AdvertiserService} from '../../../advertiser/services/advertiser.service';
import {MatTableDataSource} from '@angular/material/table';
import {SelectionModel} from '@angular/cdk/collections';

@Component({
  selector: 'ui-flight-sites-connect',
  templateUrl: './flight-sites-connect.component.html',
  styleUrls: ['./flight-sites-connect.component.scss']
})
export class FlightSitesConnectComponent implements OnInit {

  wait: boolean;
  sites: MatTableDataSource<any>;
  selection = new SelectionModel<any>(true, []);
  readonly displayedColumns = ['select', 'name', 'uniqueUsers'];

  constructor(@Inject(MAT_DIALOG_DATA) public data: any,
              private advertiserService: AdvertiserService) {
  }

  ngOnInit(): void {
    this.loadSitesList();
  }

  async loadSitesList(): Promise<any> {
    try {
      this.wait = true;
      const sites = await this.advertiserService.getSiteList(this.data.advertiserId);
      if (sites && sites.length) {
        this.sites = new MatTableDataSource(sites);
        if (this.data.siteIds && this.data.siteIds.length) {
          this.data.siteIds.forEach(id => this.selection.select(id));
        }
      }
    } catch (e) {
      console.error(e);
    } finally {
      this.wait = false;
    }
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
      this.sites.data.forEach(row => this.selection.select(row.id));
    }
  }
}
