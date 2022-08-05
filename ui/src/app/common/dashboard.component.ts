import {Component} from '@angular/core';
import {UserSessionModel} from '../user/models/user-session.model';

@Component({
  selector: 'ui-dashboard',
  templateUrl: 'dashboard.html'
})

export class DashboardComponent {

  user = new UserSessionModel();

  constructor() {
  }
}
