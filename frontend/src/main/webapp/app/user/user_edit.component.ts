import { Component, OnDestroy, OnInit }         from '@angular/core';
import { Subscription }                         from 'rxjs';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { IconComponent }           from '../shared/icon.component';
import { LoadingComponent }        from '../shared/loading.component';
import { PageComponent }           from '../shared/page.component';
import { OptionTransferComponent } from '../shared/option_transfer.component';
import { L10nUserRoles }               from '../common/L10n.const';
import { UserService }             from './user.service';
import { UserModel }               from './user.model';
import { UserRoleModel }           from './user_role.model';

@Component({
    selector: 'ui-user-edit',
    templateUrl: 'edit.html'
})

export class UserEditComponent extends PageComponent implements OnInit, OnDestroy{

    public title:string;

    private mode: string;
    public wait: boolean       = true;
    private waitSubmit: boolean = false;
    private routerSubscription: Subscription;
    private accountId: number;
    private roles: Array<UserRoleModel>;
    private advLevelAccessAvailable: boolean = false;
    private user: UserModel;
    private backUrl: string;
    private errors  = {
        firstName: null,
        lastName: null,
        email: null,
        advertiserIds: null
    };

    private radioVal: boolean   = false;
    private advertisers: Array<any>;
    private advertisersAvailable: Array<any> = [];
    private advertisersSelected: Array<any> = [];
    private sort  = function (a, b) {
        if (a.name === b.name) return 0;
        return (a.name > b.name) ? 1 : -1;
    };

    public constructor(
        private userService: UserService,
        private route: ActivatedRoute,
        private router: Router){

        super();

        if (this.mode) {
            this.initTitle();
        }
    }

    public L10nUserRoles = L10nUserRoles;

    ngOnInit(){
        this.routerSubscription   = this.route.url.subscribe(params => {
            this.accountId      = +params[0].path || null;
            this.backUrl        = (/agency/.test(location.href) ? '/agency/' : '/advertiser/')+this.accountId+'/account';

            if (params[2] && params[2].path === 'add') {
                this.mode       = 'add';
                this.initTitle();

                this.userService.getUserRoles(this.accountId)
                    .then(roles => {
                        this.roles          = roles;
                        this.user           = new UserModel();
                        this.user.accountId = this.accountId;
                        this.user.roleId      = roles[0].id;
                        this.loadAdvertisers();
                        this.wait           = false;
                    });
            } else {
                this.mode       = 'edit';
                this.initTitle();

                this.userService.getUserRoles(this.accountId)
                    .then(roles => {
                        this.roles          = roles;
                        this.userService.getUserById(+params[2].path)
                            .then(user => {
                                this.user   = user;
                                this.loadAdvertisers();
                                this.initAdvertisers();
                                this.initTitle();

                                this.wait   = false;
                            });
                    });
            }
        });
    }

    private initTitle(): void {
        if (this.mode === 'add') {
            this.title = '_L10N_(agencyAccount.user.createNew)';
        } else {
            if (this.user) {
                this.title = `${'_L10N_(agencyAccount.user.edit)'}: ${this.user.firstName} ${this.user.lastName}`;
            } else {
                this.title = '_L10N_(agencyAccount.user.edit)';
            }
        }
    }

    ngOnDestroy(){
        if (this.routerSubscription){
            this.routerSubscription.unsubscribe();
        }
    }

    private doSubmit(e: any){
        e.preventDefault();
        if (this.waitSubmit) return;

        this.waitSubmit = true;
        let method  = this.mode === 'add' ? 'save' : 'update';
        this.userService[method](this.user)
            .then(res   => {
                this.router.navigate([this.backUrl]);
            })
            .catch(e => {
                this.waitSubmit = false;
                this.errors = e.json();
            });

    }

    private setAdvLevelAccessAvailable(newRoleId: any){
        this.advLevelAccessAvailable = this.advertisers && this.advertisers.length > 0 &&
            this.roles
            .filter( r => r.id == newRoleId )
            .map( r => r.advLevelAccessAvailable )[0];
    }

    private initAdvertisers(){
        if (this.user.advertiserIds && this.user.advertiserIds.length){
            this.radioVal   = true;
        }
    }

    private loadAdvertisers(): Promise<any>{
        this.wait   = true;

        let advertisers;
        if (this.user.id) {
            advertisers = this.userService.getAdvertisersByUser(this.user.id)
        } else {
            advertisers = this.userService.getAdvertisersByAgency(this.accountId)
        }
        return advertisers
            .then(list => {
                this.advertisers  = list;
                this.setAdvLevelAccessAvailable(this.user.roleId);
                this.initOptiontransfer();
                this.wait   = false;
            });
    }

    private initOptiontransfer(){
        this.advertisersAvailable = [];
        this.advertisersSelected  = [];

        this.advertisers.forEach(v => {
            if (this.user.advertiserIds.indexOf(v.id) !== -1){
                this.advertisersSelected.push({
                    id: v.id,
                    name: v.name
                });
            } else {
                this.advertisersAvailable.push({
                    id: v.id,
                    name: v.name
                });
            }
        });
    }

    private onUserRoleChange(newRoleId: any){
        this.setAdvLevelAccessAvailable(newRoleId);
        if (!this.advLevelAccessAvailable) {
            this.clearAdvertiserIds();
        }
    }

    private switchType(e: any){
        this.radioVal = !this.radioVal;
        if (!this.radioVal){
            this.clearAdvertiserIds();
        }
    }

    private onAdvertisersListChange(e: any){
        if (e.length === this.advertisers.length || e.length === 0){
            this.clearAdvertiserIds();
        } else {
            this.refreshAdvertiserIds(e);
        }
    }

    private clearAdvertiserIds(){
        this.user.advertiserIds = [];
        this.initOptiontransfer();
        this.radioVal = false;
    }

    private refreshAdvertiserIds(e: any){
        let ids = [];
        for (let v of e){
            ids.push(v.id);
        }
        this.user.advertiserIds = ids;
    }
}
