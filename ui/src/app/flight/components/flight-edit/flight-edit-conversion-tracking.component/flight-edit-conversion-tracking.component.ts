import {Component, Input, Output, EventEmitter, OnInit} from '@angular/core';
import {ConversionService} from '../../../../conversion/services/conversion.service';
import {AdvertiserSessionModel} from '../../../../advertiser/models';
import {MatSelectionListChange} from '@angular/material/list';
import {map} from "rxjs/operators";


@Component({
  selector: 'ui-flight-edit-conversions',
  templateUrl: './flight-edit-conversion-tracking.component.html',
  styleUrls: ['./flight-edit-conversion-tracking.component.scss']
})
export class FlightEditConversionsComponent implements OnInit {

  _ids

  @Input()
  set conversionIds(ids: number[]) {
    this._ids = ids;
    this.init()
  }

  get conversionIds(): number[] {
    return this._ids;
  }

  // @Input() conversionIds: ;
  @Output() conversionChange = new EventEmitter();

  wait = false;
  conversions: Array<any>;

  constructor(private conversionsService: ConversionService) {
  }

  ngOnInit(): void {
    // this.init()
  }

  init(){
    this.conversionsService.getListByAdvertiserId$(new AdvertiserSessionModel().id)
        .pipe(map(list => list.map(item => {
          item._id = item.conversion.id;
          return item;
    }))).subscribe(list => {
      this.conversions = list
          .filter(v => this._ids.includes(v.conversion.id))
          .map(v => v.conversion.id);
      this.conversionChange.emit(this._ids);

      list.forEach(v => {
        v.checked = (this._ids.includes(v.conversion.id));
      });
      this.conversions = list;
    });
  }

  selectConversion(e: MatSelectionListChange): void {
    const item = e.option.value;
    item.checked = !item.checked;
    this._ids = this.conversions.filter(v => v.checked).map(v => v.conversion.id);

    this.conversionChange.emit(this._ids);
  }
}
