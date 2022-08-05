import { Component, OnInit }    from '@angular/core';
import { Router, RouterModule } from '@angular/router';

import { PageComponent } from '../shared/page.component';

@Component({
    selector: 'ui-error',
    templateUrl: 'error.html',
})

export class ErrorComponent extends PageComponent implements OnInit {

    public title:string = 'Error Page';
    public type:number = 0;

    constructor(private router: Router){
        super();
    }

    ngOnInit(){
        switch (this.router.url){
            case '/error/403':
                this.type = 403;
                break;
            case '/error/404':
                this.type = 404;
                break;
            case '/error/500':
                this.type = 500;
                break;
            default:
                this.type = 0;
        }
    }

    public goBack(e?: any){
        this.router['location'].back();
    }
}
