import {Component, Input} from '@angular/core';
import {environment} from "../../../../environments/environment";

@Component({
  selector: 'ui-loading',
  styleUrls: ['./loading.component.scss'],
  templateUrl: './loading.component.html'
})
export class LoadingComponent {
  public hostName: 'Default' | 'Genius' | 'Pharmatic';
  @Input() overlay = false;

  constructor() {
    this.hostName = 'Default';
  }

  ngOnInit(): void {
    switch (window.location.protocol + '//' + window.location.hostname) {
      case environment.hostGenius:
        this.hostName = 'Genius';
        break;
      case environment.hostPharmatic:
        this.hostName = 'Pharmatic';
        break;
    }
  }
}
