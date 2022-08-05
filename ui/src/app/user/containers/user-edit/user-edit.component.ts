import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from 'rxjs';
import {ActivatedRoute, Router} from '@angular/router';
import {L10nUserRoles} from '../../../common/L10n.const';
import {UserService} from '../../services/user.service';
import {ErrorHelperStatic} from '../../../shared/static/error-helper.static';
import {ArrayHelperStatic} from '../../../shared/static/array-helper.static';

@Component({
  selector: 'ui-user-edit',
  templateUrl: './user-edit.component.html',
  styleUrls: ['./user-edit.component.scss']
})
export class UserEditComponent implements OnInit, OnDestroy {

  mode: string;
  wait = true;
  waitSubmit = false;
  roles: UserRoleModel[];
  advLevelAccessAvailable = false;
  user: UserModel = {
    accountId: null,
    firstName: null,
    lastName: null,
    roleId: null,
    advertiserIds: []
  };
  backUrl: string;
  errors: any;
  matcher = ErrorHelperStatic.getErrorMatcher;
  L10nUserRoles = L10nUserRoles;
  radioVal = false;
  sort = ArrayHelperStatic.sortByKey.bind(null, 'name');
  advertisersAvailable = [];
  advertisersSelected = [];
  private routerSubscription: Subscription;
  private accountId: number;
  private advertisers: any[];

  constructor(private userService: UserService,
              private route: ActivatedRoute,
              private router: Router) {
  }

  ngOnInit(): void {
    this.routerSubscription = this.route.url.subscribe(params => {
      this.accountId = +params[0].path || null;
      this.backUrl = (/agency/.test(location.href) ? '/agency/' : '/advertiser/') + this.accountId + '/account';

      if (params[2] && params[2].path === 'add') {
        this.mode = 'add';

        this.userService.getUserRoles(this.accountId)
          .then(roles => {
            this.roles = roles;
            this.user = {
              ...this.user,
              accountId: this.accountId,
              roleId: roles[0].id
            };
            this.loadAdvertisers();
            this.wait = false;
          });
      } else {
        this.mode = 'edit';

        this.userService.getUserRoles(this.accountId)
          .then(roles => {
            this.roles = roles;
            this.userService.getUserById(+params[2].path)
              .then(user => {
                this.user = user;
                this.loadAdvertisers();
                this.initAdvertisers();

                this.wait = false;
              });
          });
      }
    });
  }

  ngOnDestroy(): void {
    if (this.routerSubscription) {
      this.routerSubscription.unsubscribe();
    }
  }

  doSubmit(e: Event): void {
    e.preventDefault();

    if (this.waitSubmit) {
      return;
    }

    this.waitSubmit = true;
    const method = this.mode === 'add' ? 'save' : 'update';
    this.userService[method](this.user)
      .then(() => {
        this.router.navigate([this.backUrl]);
      })
      .catch(err => {
        this.errors = ErrorHelperStatic.matchErrors(err);
        this.waitSubmit = false;
      });
  }

  setAdvLevelAccessAvailable(newRoleId: any): void {
    this.advLevelAccessAvailable = this.advertisers && this.advertisers.length > 0 &&
      this.roles
        .filter(r => r.id === newRoleId)
        .map(r => r.advLevelAccessAvailable)[0];
  }

  initAdvertisers(): void {
    if (this.user.advertiserIds && this.user.advertiserIds.length) {
      this.radioVal = true;
    }
  }

  loadAdvertisers(): Promise<any> {
    this.wait = true;

    let advertisers;
    if (this.user.id) {
      advertisers = this.userService.getAdvertisersByUser(this.user.id);
    } else {
      advertisers = this.userService.getAdvertisersByAgency(this.accountId);
    }
    return advertisers
      .then(list => {
        this.advertisers = list;
        this.setAdvLevelAccessAvailable(this.user.roleId);
        this.initOptionTransfer();
        this.wait = false;
      });
  }

  initOptionTransfer(): void {
    this.advertisersAvailable = [...this.advertisers];
    this.advertisersSelected = this.advertisers.filter(item => this.user.advertiserIds.includes(+item.id));
  }

  onUserRoleChange(newRoleId: any): void {
    this.setAdvLevelAccessAvailable(newRoleId);
    if (!this.advLevelAccessAvailable) {
      this.clearAdvertiserIds();
    }
  }

  switchType(e: any): void {
    this.radioVal = !this.radioVal;
    if (!this.radioVal) {
      this.clearAdvertiserIds();
    }
  }

  onAdvertisersListChange(list: any[]): void {
    if (list.length === this.advertisers.length || list.length === 0) {
      this.clearAdvertiserIds();
    } else {
      this.refreshAdvertiserIds(list);
    }
  }

  clearAdvertiserIds(): void {
    this.user.advertiserIds = [];
    this.radioVal = false;
  }

  refreshAdvertiserIds(list: any[]): void {
    this.user.advertiserIds = list.map(item => +item.id).filter(id => !!id);
  }
}
