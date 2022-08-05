import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import { IDropdownSettings } from 'ng-multiselect-dropdown';

@Component({
  selector: 'ui-search-multiselect-list',
  templateUrl: './search-multiselect-list.component.html',
  styleUrls: ['./search-multiselect-list.component.scss']
})
export class SearchMultiselectListComponent implements OnInit, OnDestroy {
  @Input() data: any[] | any = [];
  @Input() dropdownSettings: IDropdownSettings = {};
  @Input() placeholder: string;

  public selectedOptions: any[];

  @Output() search: EventEmitter<string> = new EventEmitter();
  @Output() ngModelChange: EventEmitter<any> = new EventEmitter();

  constructor() {
    this.selectedOptions = [];
  }

  ngOnInit(): void {}

  ngOnDestroy(): void {}

  public onSearch(event): void {
    this.search.emit(event);
  }

  public onNgModelChange(): void {
    this.ngModelChange.emit(this.selectedOptions);
  }
}
