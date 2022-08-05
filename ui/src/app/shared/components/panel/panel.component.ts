import {Component, Input, Output, EventEmitter} from '@angular/core';

export interface IFlightPart {
  enum: string;
  flight: any;
}

@Component({
  selector: 'ui-panel',
  styleUrls: ['./panel.component.scss'],
  templateUrl: './panel.component.html'
})
export class PanelComponent {
  @Input() title = '';
  @Input() icon = '';
  @Input() hint: string;
  @Input() collapsible: boolean;
  @Input() collapsed: boolean;
  @Input() showApplyButton: IFlightPart;
  @Output() panelToggle = new EventEmitter();
  @Output() apply = new EventEmitter();

  onApply(event: MouseEvent) {
    event.stopPropagation();
    this.apply.emit(this.showApplyButton);
  }
}
