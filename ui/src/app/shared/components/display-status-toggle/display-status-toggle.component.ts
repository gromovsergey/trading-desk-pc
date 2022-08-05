import {Component, Input, Output, EventEmitter, OnChanges} from '@angular/core';

@Component({
  selector: 'ui-display-status-toggle',
  templateUrl: './display-status-toggle.component.html'
})
export class DisplayStatusToggleComponent implements OnChanges {

  @Input() status: string;
  @Input() statusObject: any;
  @Output() statusChange: EventEmitter<any> = new EventEmitter();

  direction: boolean;
  private activeStatus = ['LIVE', 'NOT_LIVE', 'LIVE_NEED_ATT'];

  ngOnChanges(): void {
    this.direction = this.activeStatus.includes(this.status.split('|')[0]);
  }

  changeStatus(e: any): void {
    e.preventDefault();

    const currentStatus = this.statusObject.displayStatus.split('|')[0];

    this.statusObject.statusChangeOperation = this.activeStatus.includes(currentStatus) ? 'INACTIVATE' : 'ACTIVATE';
    this.statusObject.displayStatus = this.status = this.activeStatus.includes(currentStatus) ? 'INACTIVE' : 'LIVE';
    this.statusChange.emit(this.statusObject);
  }
}
