import {Directive, ElementRef, Input, OnChanges} from '@angular/core';
import {DISPLAY_STATUS_COLORS} from '../../const';
import {L10nStatic} from '../static/l10n.static';

@Directive({
  selector: '[appDisplayStatus]',
})
export class DisplayStatusDirective implements OnChanges {

  @Input() displayStatus: string;
  @Input() displayStatusType = 'color';

  private el: HTMLElement;
  private colors = DISPLAY_STATUS_COLORS;


  constructor(el: ElementRef) {
    this.el = el.nativeElement;
    this.el.classList.add('app-display-status');
  }

  ngOnChanges(): void {
    this.switchDisplayStatus(this.displayStatus);
  }

  switchDisplayStatus(status: string): void {
    if (status === undefined) {
      return;
    }

    let elStyle = 'color';
    if (this.displayStatusType === 'bg') {
      elStyle = 'backgroundColor';
      this.el.style.color = '#fff';
    }
    this.el.classList.add('display-status');

    if (status) {
      const aStatus = status.split('|');

      switch (aStatus[0]) {
        case 'LIVE':
          this.el.style[elStyle] = this.colors.green;
          this.el.title = L10nStatic.translate('majorStatus.LIVE') + this.getTitle(aStatus[1]);
          break;
        case 'LIVE_NEED_ATT':
          this.el.style[elStyle] = this.colors.amber;
          this.el.title = L10nStatic.translate('majorStatus.LIVE') + this.getTitle(aStatus[1]);
          break;
        case 'NOT_LIVE':
          this.el.style[elStyle] = this.colors.red;
          this.el.title = L10nStatic.translate('majorStatus.NOT_LIVE') + this.getTitle(aStatus[1]);
          break;
        case 'DELETED':
          this.el.style[elStyle] = this.colors.gray;
          this.el.title = L10nStatic.translate('majorStatus.DELETED') + this.getTitle(aStatus[1]);
          break;
        case 'INACTIVE':
        default:
          this.el.style[elStyle] = this.colors.gray;
          this.el.title = L10nStatic.translate('majorStatus.INACTIVE') + this.getTitle(aStatus[1]);
      }
    }
  }

  getTitle(status: string): string {
    switch (status) {
      case 'overdraft':
        return ' — ' + L10nStatic.translate('status.messages.overdraft');
      case 'noBillingContact':
        return ' — ' + L10nStatic.translate('status.messages.noBillingContact');
      default:
        return '';
    }
  }
}
