import { Component, Input } from '@angular/core';
import { IconComponent }    from '../shared/icon.component';

@Component({
    selector: 'ui-field',
    templateUrl: 'field.html'
})

export class FieldComponent {

    @Input() title: string;

}