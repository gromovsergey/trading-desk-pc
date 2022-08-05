import { Component, Input, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { IconComponent }    from './icon.component';
import { jQuery as $ }      from '../common/common.const';

@Component({
    selector: 'ui-hint',
    template: `<div #hint class="c-hint" [ngClass]="{'c-hint-left': toLeft, 'c-hint-right': !toLeft}">
                  <ui-icon class="c-hint__icon" [icon]="icon"></ui-icon>
                  <div class="c-hint__tooltip" role="tooltip">
                      <div class="c-hint__text c-hint__text-{{size}}" [innerHTML]="text"></div>
                  </div>
              </div>`
})

export class HintComponent implements AfterViewInit {
    @ViewChild('hint') hintElement: ElementRef;
    @Input() text: string;
    @Input() size = 'sm';
    @Input() icon = 'info-circle';
    public toLeft = false;

    public get hintX(): number {
        return this.hintElement.nativeElement.getBoundingClientRect().x;
    }

    public switchHint(): void {
        var timeout;
        timeout = timeout==undefined ? setTimeout(() => {
            let winWidth = $(window).width();
            this.toLeft = (winWidth - this.hintX < 500) ? true : false;
            timeout = undefined;
        }, 50) : timeout;
    }

    ngAfterViewInit() {
        $(window).resize(this.switchHint.bind(this));
        this.switchHint();
    }
}
