import { Directive, ElementRef, Input, AfterViewInit } from '@angular/core';

@Directive({
    selector: '[numFormat]'
})

export class NumFormatDirective implements AfterViewInit{

    @Input() numFormat: boolean = false;
    private el: HTMLElement;

    constructor(el: ElementRef) {
        this.el = el.nativeElement;
    }

    ngAfterViewInit(){
        let val = this.el.textContent ? +this.el.textContent : 0;
        this.el.textContent = (this.numFormat ? val.toFixed(2) : val).toString();
    }
}