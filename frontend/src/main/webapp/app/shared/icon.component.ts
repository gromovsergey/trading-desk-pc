import { Component, Input } from '@angular/core';

@Component({
    selector: 'ui-icon',
    template: `<i *ngIf="icon && icon.length" class="fa fa-{{icon}}" [ngClass]="{'fa-fw': fw, 'fa-spin': spin, 'fa-2x': large, 'fa-flip-horizontal': fliph}"></i>`
})

export class IconComponent {
    @Input() fw: boolean    = false;
    @Input() spin: boolean  = false;
    @Input() large: boolean = false;
    @Input() fliph: boolean = false;
    @Input() icon: string   = 'circle';
}