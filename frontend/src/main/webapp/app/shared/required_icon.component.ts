import { Component }     from '@angular/core';
import { IconComponent } from './icon.component';

@Component({
    selector: 'ui-icon-required',
    template: `<sup class="h-required"><ui-icon [icon]="'asterisk'" [fw]="true"></ui-icon></sup>`
})

export class IconRequiredComponent {}