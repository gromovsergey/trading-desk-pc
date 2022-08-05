import { Component }    from '@angular/core';
import { RouterModule } from '@angular/router';

import { PageComponent }       from '../shared/page.component';
import { LoadingComponent }    from '../shared/loading.component';
import { FlightEditComponent } from '../flight/flight_edit.component';


@Component({
    selector: 'ui-lineitem-edit',
    templateUrl: 'edit.html'
})
export class LineItemEditComponent extends PageComponent{

    public title:string = 'Line Item Edit';

    public constructor(){
        super();
    }


}