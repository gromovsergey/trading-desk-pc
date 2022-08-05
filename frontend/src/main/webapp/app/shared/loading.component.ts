import { Component, Input } from '@angular/core';

import { IconComponent }    from './icon.component';

@Component({
    selector: 'ui-loading',
    template: `<div class="c-loader" [ngClass]="{'c-overlay': overlay}"><span><ui-icon [icon]="'spinner'" [spin]="true"></ui-icon> {{'_L10N_(messages.loading)'}}...</span></div>`
})

export class LoadingComponent {
    @Input() overlay: boolean = false;

    constructor() {
    }
}
