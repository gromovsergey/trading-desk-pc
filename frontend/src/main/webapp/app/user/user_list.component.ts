import { Component, Input }             from '@angular/core';
import { RouterModule }                 from '@angular/router';

import { LoadingComponent }             from '../shared/loading.component';
import { IconComponent }                from '../shared/icon.component';
import { HintComponent }                from '../shared/hint.component';
import { DisplayStatusToggleComponent } from '../shared/display_status_toggle.component';
import { DisplayStatusDirective }       from '../shared/display_status.directive';
import { L10nUserRoles }                    from '../common/L10n.const';
import { UserService }                  from './user.service';

@Component({
    selector: 'ui-user-list',
    templateUrl: 'list.html'
})

export class UserListComponent {

    @Input() accountId: number;
    @Input() showAdvertisers: boolean;

    private userList;
    private canEditUsers : boolean;
    private editUrl: string;
    public wait: boolean   = false;

    public L10nUserRoles = L10nUserRoles;

    public constructor(private userService: UserService) {
    }

    ngOnInit(){
        this.loadUserList();
        this.editUrl    = (/agency/.test(location.href) ? '/agency/': '/advertiser/')+this.accountId+'/user/';
    }

    private loadUserList() {
        this.wait = true;
        Promise.all([
            this.userService.getAccountUsers(this.accountId),
            this.userService.isAllowedLocal(this.accountId, 'user.create')
        ]).then(res => {
            this.userList = res[0];
            this.canEditUsers = Boolean(res[1]);
            this.wait = false;
        });
    }

    private changeStatus(user: any){
        this.userService
            .statusChange(user.id, user.statusChangeOperation)
            .then(newStatus => {
                user.displayStatus  = newStatus;
            });
    }
}
