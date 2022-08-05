import { Component, Input, ContentChild, ViewChild, ElementRef, OnInit, Output, EventEmitter } from '@angular/core';

import { IconComponent } from './icon.component';
import { HintComponent } from './hint.component';

@Component({
    selector: 'ui-panel',
    templateUrl: 'panel.html'
})

export class PanelComponent implements OnInit{

    @Input() title:string   = '';
    @Input() icon:string   = '';
    @Input() hint:string;
    @Input() required: boolean;
    @Input() collapsible: boolean;
    @Input() collapsed: boolean;

    @Output() onToggle = new EventEmitter();

    @ContentChild('body') bodyContentEl: ElementRef;
    @ViewChild('body') bodyChildEl: ElementRef;
    @ContentChild('titlePanel') titlePanelContentEl: ElementRef;
    @ViewChild('titlePanel') titlePanelChildEl: ElementRef;


    ngOnInit(){
        this.bodyChildEl.nativeElement.appendChild(this.bodyContentEl.nativeElement);
        if (this.titlePanelContentEl){
            this.titlePanelChildEl.nativeElement.appendChild(this.titlePanelContentEl.nativeElement);
        }
    }

    public toggle(e){
        if (this.collapsible){
            this.collapsed  = !this.collapsed;
        }
        this.onToggle.emit(this.collapsed);
    }
}
