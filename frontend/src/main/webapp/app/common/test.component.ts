import { Component } from '@angular/core';

import { PageComponent }  from '../shared/page.component';
import { ChartComponent } from '../chart/chart.component';

@Component({
    selector: 'ui-test',
    templateUrl: 'bootstrap.test.html'
})

export class TestComponent extends PageComponent{

    public title:string = 'Test Page';
}