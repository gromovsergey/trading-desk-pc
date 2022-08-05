import {Directive, ElementRef, Input, AfterViewInit} from '@angular/core';

@Directive({
  selector: '[appNumberFormat]'
})
export class NumberFormatDirective implements AfterViewInit {

  @Input() numFormat = false;

  private el: HTMLElement;

  constructor(el: ElementRef) {
    this.el = el.nativeElement;
  }

  ngAfterViewInit(): void {
    const val = this.el.textContent ? +this.el.textContent : 0;
    this.el.textContent = (this.numFormat ? val.toFixed(2) : val).toString();
  }
}
