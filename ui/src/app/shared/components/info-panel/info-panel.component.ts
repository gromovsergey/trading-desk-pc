import {Component, ComponentRef, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';

@Component({
  selector: 'ui-info-panel',
  styleUrls: ['./info-panel.component.scss'],
  templateUrl: './info-panel.component.html'
})
export class InfoPanelComponent implements OnInit, OnDestroy {

  @Input() type: 'info' | 'danger' | 'warning' | 'success';

  @Output() close: EventEmitter<boolean> = new EventEmitter<boolean>();

  public hidden: boolean;
  classIconList = {
    info: 'info',
    danger: 'error',
    warning: 'warning',
    success: 'check_circle'
  };

  ngOnInit(): void {
    this.hidden = false;
  }

  onClose() {
    this.close.emit(true);

    this.hidden = true;
  }

  ngOnDestroy(): void {
  }
}
