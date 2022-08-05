import { Component, Input }   from '@angular/core';
import { jQuery as $ }        from './common.const'

@Component({
  selector: 'ui-table-filter',
  template: `<input #filter class="form-control ex-row-filter" type="text"
            placeholder="_L10N_(common.rowFilter)" (keyup)="filterRows(filter.value)">`
})

export class TableFilterComponent {
  @Input() tableId: string;

  private get table(): any {
    return $(`#${this.tableId}`);
  }

  public filterRows(value: string): void {
    this.table
      .find('tbody tr')
      .hide()
      .filter(value ?
        (i, node) => $(node)
          .text()
          .toUpperCase()
          .indexOf(value.toUpperCase()) > -1
        : '*')
      .show();
  }
}
