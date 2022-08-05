import {Component, Input, OnInit} from '@angular/core';
import {L10nUserRoles} from '../../../common/L10n.const';
import {UserService} from '../../services/user.service';

@Component({
  selector: 'ui-user-list',
  templateUrl: 'user-list.component.html',
  styleUrls: ['./user-list.component.scss']
})
export class UserListComponent implements OnInit {

  @Input() accountId: number;
  @Input() showAdvertisers: boolean;

  userList;
  canEditUsers: boolean;
  editUrl: string;
  wait = false;
  L10nUserRoles = L10nUserRoles;

  get displayedColumns(): string[] {
    return ['name', 'email', 'role',
      ...(this.showAdvertisers ? ['advertisersAccess'] : []), 'action'];
  }

  constructor(private userService: UserService) {
  }

  ngOnInit(): void {
    this.loadUserList();
    this.editUrl = (/agency/.test(location.href) ? '/agency/' : '/advertiser/') + this.accountId + '/user';
  }

  loadUserList(): void {
    this.wait = true;
    Promise.all([
      this.userService.getAccountUsers(this.accountId),
      this.userService.isAllowedLocal(this.accountId, 'user.create')
    ]).then(res => {
      this.userList = res[0];
      this.canEditUsers = !!res[1];
      this.wait = false;
    });
  }

  changeStatus(user: any): void {
    this.userService
      .statusChange(user.id, user.statusChangeOperation)
      .then(newStatus => {
        user.displayStatus = newStatus;
      });
  }
}
