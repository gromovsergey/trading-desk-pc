import {Component, Input, OnChanges, Output, EventEmitter} from '@angular/core';
import {DropdownButtonMenuItem} from '../dropdown-button/dropdown-button.component';
import {L10nStatic} from '../../static/l10n.static';

@Component({
  selector: 'ui-display-status-btn',
  templateUrl: './display-status-button.component.html',
  styleUrls: ['./display-status-button.component.scss']
})
export class DisplayStatusButtonComponent implements OnChanges {

  @Input() status: string;
  @Input() url: string;
  @Output() statusChange = new EventEmitter();

  menuActive;
  menuInactive;
  menuDeleted;
  statusList: string[];
  menu: DropdownButtonMenuItem[];
  title: string;
  statusTypes;

  constructor() {
    this.menuActive = [
      new DropdownButtonMenuItem(L10nStatic.translate('status.button.deactivate'), {}, DEACTIVATE_OP),
      new DropdownButtonMenuItem(L10nStatic.translate('status.button.delete'), {}, DELETE_OP),
    ];
    this.menuInactive = [
      new DropdownButtonMenuItem(L10nStatic.translate('status.button.activate'), {}, ACTIVATE_OP),
      new DropdownButtonMenuItem(L10nStatic.translate('status.button.delete'), {}, DELETE_OP),
    ];
    this.menuDeleted = [
      new DropdownButtonMenuItem(L10nStatic.translate('status.button.restore'), {}, RESTORE_OP),
    ];

    this.statusTypes = {
      LIVE: {title: L10nStatic.translate('majorStatus.LIVE'), menu: this.menuActive},
      LIVE_NEED_ATT: {title: L10nStatic.translate('majorStatus.LIVE'), menu: this.menuActive},
      DELETED: {title: L10nStatic.translate('majorStatus.DELETED'), menu: this.menuDeleted},
      NOT_LIVE: {title: L10nStatic.translate('majorStatus.NOT_LIVE'), menu: this.menuInactive},
      INACTIVE: {title: L10nStatic.translate('majorStatus.INACTIVE'), menu: this.menuInactive}
    };
  }

  ngOnChanges(): void {
    this.statusList = this.status.split('|');
    this.beforeChange();
  }


  beforeChange(): void {
    const type = this.statusList[0];
    this.title = this.statusTypes[type].title;
    this.menu = this.statusTypes[type].menu;

    this.menu.forEach((item) => {
      item.onclick = () => {
        this.statusChangeEvent(item.operation);
        item.deactivate();
      };
    });
  }

  statusChangeEvent(name: string): void {
    this.statusChange.emit(name);
  }
}

const ACTIVATE_OP = 'ACTIVATE';
const DEACTIVATE_OP = 'DEACTIVATE';
const DELETE_OP = 'DELETE';
const RESTORE_OP = 'RESTORE';
