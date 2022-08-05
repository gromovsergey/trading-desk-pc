import {Component, OnInit, ViewChild, ElementRef} from '@angular/core';
import {LS_EMAIL, SESSION_LAST_URL} from '../const';
import {Router} from '@angular/router';
import { environment } from 'src/environments/environment';


@Component({
  selector: 'ui-logo',
  styleUrls: ['./logo.component.scss'],
  templateUrl: './logo.component.html'
})
export class LogoComponent implements OnInit {
  public hostName: 'Default' | 'Genius' | 'Pharmatic';

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
