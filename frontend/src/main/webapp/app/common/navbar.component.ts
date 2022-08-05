import { Component, OnInit, Renderer, Output, EventEmitter, Input } from '@angular/core';
import { RouterModule }                                             from '@angular/router';

import { IconComponent }          from '../shared/icon.component';
import { DisplayStatusDirective } from '../shared/display_status.directive';
import { L10nSearchTypes }        from '../common/L10n.const';
import { UserSessionModel }       from '../user/user_session.model';

import { QuickSearchService }     from './quick_search.service';

@Component({
    selector: 'ui-navbar',
    providers: [QuickSearchService],
    templateUrl: 'navbar.html'
})

export class NavbarComponent implements OnInit {

    @Output() onXsMenuShow: EventEmitter<Object>  = new EventEmitter();
    @Input() xsMenuVisible: boolean = false;

    private quickSearchText: string;
    private quickSearchBlock: boolean = false;
    private quickSearchWait: boolean = false;
    private quickSearchData: Array<Object>;
    private quickSearchInputDelay: number   = 1000;
    private quickSearchTextMinLength: number   = 3;

    private menuUserVisible: boolean = false;
    public userData: UserSessionModel = new UserSessionModel();
    private userBtnId: string = 'navUserBtn';

    public L10nSearchTypes = L10nSearchTypes;

    constructor(private renderer: Renderer,
                private quickSearchService: QuickSearchService){

    }

    ngOnInit(){
        this.renderer.listenGlobal('document', 'click', (e: any) => { // global blur for menu
            if (this.menuUserVisible && e.target.id !== this.userBtnId && e.target.parentElement.id !== this.userBtnId){
                this.menuUserVisible    = false;
            }
            this.quickSearchData    = null;
        });
    }

    public showUserMenu(){
        this.menuUserVisible = true;
    }

    public hideMenus(){
        this.menuUserVisible    = false;
        this.quickSearchData    = null;
    }

    public quickSearch(){
        let self    = this;
        if (this.quickSearchBlock) return;

        this.quickSearchBlock   = true;
        setTimeout(()=>{
            self.quickSearchBlock = false;
            if (self.quickSearchText.length >= this.quickSearchTextMinLength){
                this.quickSearchData = null;
                this.quickSearchWait = true;
                this.quickSearchService
                    .search(this.quickSearchText)
                    .then(quickSearchData => {
                        this.quickSearchData = quickSearchData;
                        this.quickSearchWait = false;
                    });
            }
        }, this.quickSearchInputDelay);
    }

    public getLinkByTypeAndId(type: string, id: number): string {
        switch (type) {
            case 'Agency': return '/agency/'+id+'/advertisers';
            case 'Advertiser': return '/advertiser/'+id+'/flights';
            case 'Flight': return '/flight/'+id;
        }
        return '';
    }

    public xsMenuShow(e: any){
        this.onXsMenuShow.emit(this.xsMenuVisible  = !this.xsMenuVisible);
    }

    isInternalView(){
        return this.userData.role === 'INTERNAL';
    }
}
