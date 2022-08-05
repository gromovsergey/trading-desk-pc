import {Component, Inject, OnInit} from '@angular/core';
import {CreativeService} from '../../../creative/services/creative.service';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {MatTableDataSource} from '@angular/material/table';
import {SelectionModel} from '@angular/cdk/collections';

@Component({
  selector: 'ui-flight-creatives-connect',
  templateUrl: 'flight-creatives-connect.component.html',
  styleUrls: ['./flight-creatives-connect.component.scss']
})
export class FlightCreativesConnectComponent implements OnInit {
  waitPopup: boolean;
  creatives: MatTableDataSource<any>;
  selection = new SelectionModel<any>(true, []);
  readonly displayedColumns = ['select', 'name', 'size', 'template'];

  constructor(private creativeService: CreativeService,
              @Inject(MAT_DIALOG_DATA) public data: any) {
  }

  ngOnInit(): void {
    this.loadCreatives();
  }

  async loadCreatives(): Promise<any> {
    this.waitPopup = true;
    this.creatives = null;

    try {
      this.creatives = new MatTableDataSource<any>(await this.creativeService.getListByAdvertiserId(this.data.advertiserId));
      if (this.data.creativeIds && this.data.creativeIds.length) {
        this.data.creativeIds.forEach(id => this.selection.select(id));
      }
    } catch (e) {
      console.error(e);
    } finally {
      this.waitPopup = false;
    }
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
      this.creatives.data.forEach(row => this.selection.select(row.id));
    }
  }
}
