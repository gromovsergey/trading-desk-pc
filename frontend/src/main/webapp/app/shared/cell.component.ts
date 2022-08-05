import { Component, Input, ContentChild, ViewChild, ElementRef, OnInit } from '@angular/core';

@Component({
    selector: 'ui-cell',
    template: `<div>
              <small class="stats-label">{{title}}</small>
              <h4 class="dropdown c-stats__cell" #body></h4>
              </div>`
})

export class CellComponent implements OnInit{

    @Input() title: string;

    @ContentChild('body') bodyContentEl: ElementRef;
    @ViewChild('body') bodyChildEl: ElementRef;


    ngOnInit(){
        this.bodyChildEl.nativeElement.appendChild(this.bodyContentEl.nativeElement);
    }

}
