import { Component }                            from '@angular/core';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';

import { PopupComponent }    from '../shared/popup.component';
import { FooterComponent }   from './footer.component';
import { NavbarComponent }   from './navbar.component';
import { MenuMainComponent } from './menu_main.component';

class Point {
    public x: number;
    public y: number;
}

@Component({
    selector: 'ui-layout',
    templateUrl: 'layout.html'
})

export class LayoutComponent {

    public xsMenuVisible: boolean = false;
    private touchStartPoint: Point;
    private touchDX: number = 50;
    private touchDY: number = 20;

    private urlSubscribe;


    constructor(private route: ActivatedRoute, private router: Router){
    }

    ngOnInit(){
        this.urlSubscribe   = this.route.url.subscribe(url => {
                setImmediate(() => {
                    sessionStorage.setItem('lu', this.router.url.toString());
                });
            });
    }

    ngOnDestroy(){
        if (this.urlSubscribe){
            this.urlSubscribe.unsubscribe();
        }
    }

    xsMenuShow(e:boolean){
        this.xsMenuVisible  = e;
    }

    touchStart(e: TouchEvent){
        this.touchStartPoint    = {
            x: e.touches[0].clientX,
            y: e.touches[0].clientY
        }
    }

    touchEnd(e: TouchEvent){
        let dx  = this.touchStartPoint.x - e.changedTouches[0].clientX,
            dy  = Math.abs(this.touchStartPoint.y - e.changedTouches[0].clientY);
        if (dx >= this.touchDX && dy <= this.touchDY){
            this.xsMenuVisible  = false;
        }
    }
}
