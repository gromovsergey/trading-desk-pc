import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Subscription} from 'rxjs';

@Component({
  selector: 'ui-error',
  templateUrl: './error.component.html',
  styleUrls: ['./error.component.scss']
})
export class ErrorComponent implements OnInit, OnDestroy {

  code: number;
  message: string;
  private codes = {
    403: 'error.forbidden',
    404: 'error.notFound',
  };
  private $route: Subscription;

  constructor(private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.$route = this.route.paramMap.subscribe(param => {
      const code = +param.get('code');
      if (this.codes[code]) {
        this.code = code;
        this.message = this.codes[code];
      }
    });
  }

  ngOnDestroy(): void {
    this.$route.unsubscribe();
  }
}
