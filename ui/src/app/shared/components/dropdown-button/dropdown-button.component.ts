import {Component, Input} from '@angular/core';

@Component({
  selector: 'ui-dropdown-btn',
  templateUrl: './dropdown-button.component.html',
  styleUrls: ['./dropdown-button.component.scss']
})
export class DropdownButtonComponent {

  @Input() title: string;
  @Input() status: string;
  @Input() icon: DropdownButtonIcon;
  @Input() menu: DropdownButtonMenuItem[];
  @Input() inline: boolean;
  @Input() disabled: boolean;

  menuClick(e: any, item?: DropdownButtonMenuItem): void {
    this.menu.forEach((v) => {
      v.deactivate();
    });

    if (item.onclick !== undefined) {
      item.activate();
      item.onclick.call(this);
    }
  }
}

export class DropdownButtonIcon {
  type: string;
  color: string;
}

export class DropdownButtonMenuItem {

  text: string;
  operation: string;
  settings: any;

  constructor(text: string, settings?: any, operation?: string) {
    this.text = text;
    this.operation = operation;
    this.settings = Object.assign({
      active: false
    }, settings);
  }

  get link(): string {
    return this.settings.link || '';
  }

  set onclick(f: any) {
    this.settings.onclick = f;
  }

  get onclick(): any {
    return this.settings.onclick || '';
  }

  get active(): boolean {
    return this.settings.active || false;
  }

  activate(): void {
    this.settings.active = true;
  }

  deactivate(): void {
    this.settings.active = false;
  }
}
