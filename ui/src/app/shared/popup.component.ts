import {Component, Input, ContentChild, ViewChild, ElementRef, OnChanges, EventEmitter, Output} from '@angular/core';

@Component({
  selector: 'ui-popup',
  templateUrl: 'popup.html'
})
export class PopupComponent implements OnChanges {

  @Input() visible = false;
  @Input() blocked = false;
  @Input() options;
  @Input() closeBtnTitle = 'Close';

  @Output() popupClose = new EventEmitter();
  @Output() save = new EventEmitter();

  @ContentChild('modal') bodyContentEl: ElementRef;
  // @ViewChild('modal') bodyChildEl: ElementRef;

  private _defaults = {
    title: '',
    icon: '',
    hint: '',
    btnTitle: '',
    btnIcon: '',
    btnIconDisabled: false,
    size: 'md'
  };

  ngOnChanges(): void {
    this.options = Object.assign(this._defaults, this.options || {});
    // this.bodyChildEl.nativeElement.appendChild(this.bodyContentEl.nativeElement);
  }

  popupHide(): void {
    this.popupClose.emit(true);
  }

  popupSave(): void {
    this.save.emit(true);
  }
}
