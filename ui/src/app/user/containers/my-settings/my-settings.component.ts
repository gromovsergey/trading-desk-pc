import {Component, OnInit} from '@angular/core';
import {UserService} from '../../services/user.service';
import {L10nStatic} from '../../../shared/static/l10n.static';
import {ErrorHelperStatic} from '../../../shared/static/error-helper.static';

@Component({
  selector: 'ui-my-settings',
  templateUrl: './my-settings.component.html'
})
export class MySettingsComponent implements OnInit {

  wait: boolean;
  showPasswordForm: boolean;
  userSettings: UserModel;
  oldPassword: string;
  newPassword: string;
  confirmNewPassword: string;
  notificationMessage: string;
  matcher = ErrorHelperStatic.getErrorMatcher;
  waitSubmit = false;
  errors: any = {
    oldPassword: null,
    newPassword: null,
    confirmNewPassword: null
  };

  constructor(private userService: UserService) {
  }

  ngOnInit(): void {
    (async () => {
      this.wait = true;
      try {
        this.userSettings = await this.userService.getProfile();
      } catch (err) {
        console.error(err);
      } finally {
        this.wait = false;
      }
    })();
  }

  doSubmit(e: any): void {
    e.preventDefault();
    if (this.waitSubmit) {
      return;
    }
    this.notificationMessage = null;

    this.waitSubmit = true;
    this.userService.passwordChange(this.oldPassword, this.newPassword, this.confirmNewPassword)
      .then(() => {
        this.oldPassword = null;
        this.newPassword = null;
        this.confirmNewPassword = null;
        this.errors.oldPassword = null;
        this.errors.newPassword = null;
        this.errors.confirmNewPassword = null;
        this.showPasswordForm = false

        this.notificationMessage = L10nStatic.translate('agencyAccount.notification.user.passwordChangeSucceeded');
        this.waitSubmit = false;
      })
      .catch(err => {
        this.waitSubmit = false;
        this.errors = ErrorHelperStatic.matchErrors(err);
      });
  }
}
