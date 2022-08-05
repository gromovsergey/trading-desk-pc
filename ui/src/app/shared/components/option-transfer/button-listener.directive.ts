import {Directive, NgZone, OnInit} from '@angular/core';

@Directive({
  selector: '[uiButtonListener]'
})
export class ButtonListenerDirective implements OnInit {

  constructor(public ngZone: NgZone) {}

  ngOnInit(): void {
    this.ngZone.runOutsideAngular(() => {
      document.addEventListener(
          'mousedown',
          this.onPointerDown.bind(this)
      );
    });
  }

  onPointerDown(event: MouseEvent): void {
    if (event.target['className'].includes('sort-up_button') || event.target['className'].includes('sort-down_button')) {
      this.setActive(event, 'a-active');
    }
    if (event.target['className'].includes('sort-up_selectedButton') || event.target['className'].includes('sort-down_selectedButton')) {
      this.setActive(event, 's-active');
    }
  }

  private setActive(event: MouseEvent, className: string): void {
    document.querySelectorAll(`.${className}`).forEach(element => {
      element.classList.remove(`${className}`);
    });
    event.target['classList'].add(`${className}`);
  }
}
