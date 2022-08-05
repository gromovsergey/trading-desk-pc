import {Component, OnInit} from '@angular/core';
import {UserService} from '../../services/user.service';
import {L10nStatic} from '../../../shared/static/l10n.static';
import {ErrorHelperStatic} from '../../../shared/static/error-helper.static';

@Component({
  selector: 'ui-status-version',
  templateUrl: './status-version.component.html'
})
export class StatusVersionComponent implements OnInit {
  data: { version: number, comment: string }[]

  constructor() {
  }

  ngOnInit(): void {
    this.data = [
      {
        version: 4,
        comment: 'fix bug with table'
      },
      {
        version: 3,
        comment: 'пунктры с 14 по 16 + 9'
      },
      {
        version: 2,
        comment: 'пункты с 5 по 13 '
      },
      {
        version: 1,
        comment: '1. change campaign\n 2.Loading element for creatives\n '
      }
    ]
  }
}
