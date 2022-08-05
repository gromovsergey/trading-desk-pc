import {Component, Input, ViewChild, ElementRef, Output, EventEmitter, OnInit, OnDestroy} from '@angular/core';
import {UserSessionModel} from '../../../user/models/user-session.model';
import {AdvertiserSessionModel} from '../../../advertiser/models';
import {Subject, Subscription} from 'rxjs';
import {debounceTime} from 'rxjs/operators';
import { MatDialog } from '@angular/material/dialog';
import {ShowWarningComponent} from "./show-warning/show-warning.component";

@Component({
  selector: 'ui-creative-option',
  templateUrl: 'creative-options.component.html',
  styleUrls: ['./creative-options.component.scss']
})
export class CreativeOptionComponent implements OnInit, OnDestroy {

  @Input() option: any;
  @Input() errors: Array<string>;
  @Output() optionChange = new EventEmitter();
  @ViewChild('option') optionEl: ElementRef;

  accountId: number = new AdvertiserSessionModel().id;
  showOption: boolean;
  change$ = new Subject();
  changePipe: Subscription;
  exceptionArr: { base: string, optionally: string }[][];
  isAlreadyChecked: string[];
  exceptionFields: {name: string, type: number}[];

  constructor(public dialog: MatDialog) {
    this.exceptionArr = [
        [
            { base: 'ads.adfox.ru', optionally: 'goLink' },
            { base: 'ad.adriver.ru', optionally: 'cgi-bin/click.cgi?' },
            { base: 'ad.doubleclick.net', optionally: 'ddm/trackclk' },
            { base: 'wcm.solution.weborama.fr', optionally: 'fcgi-bin/dispatch.fcgi?a.A=cl' }
        ],
        [
          { base: 'ads.adfox.ru', optionally: 'getCode' },
          { base: 'ad.adriver.ru', optionally: 'cgi-bin/rle.cgi?' },
          { base: 'ad.doubleclick.net', optionally: 'ddm/trackimp' },
          { base: 'wcm.solution.weborama.fr', optionally: 'fcgi-bin/dispatch.fcgi?a.A=im' }
        ]
    ];
    this.exceptionFields = [
      {name: 'CRADVTRACKPIXEL', type: 1},
      {name: 'CRADVTRACKPIXEL2', type: 1},
      {name: 'CRADVTRACKPIXEL3', type: 1},
      {name: 'CRADVTRACKPIXEL4', type: 1},
      {name: 'CRADVTRACKPIXEL5', type: 1},
      {name: 'CRCLICK', type: 0}
    ]
    this.isAlreadyChecked = [];
  }

  ngOnInit(): void {
    this.showOption = new UserSessionModel().isInternal() || !this.option.internalUse;

    if (this.option.defaultValue) {
      this.changed();
    }

    this.changePipe = this.change$.pipe(
      debounceTime(1000),
    ).subscribe(res => {
      let type = this.isExceptionField(res['token']);
      if (type || type === 0) {
        this.checkUrl(res, type);
      }
      this.optionChange.emit(res);
    });
  }

  ngOnDestroy(): void {
    this.changePipe.unsubscribe();
  }

  changed(): void {
    this.change$.next(this.option);
  }

  fileUploaded(e: any): void {
    this.option.value = e;
    this.changed();
  }

  public checkUrl(url, type): void {
    let search = this.exceptionArr[type].filter(currentUrl => {
      return url['value'].includes(currentUrl.base) && !url['value'].includes(currentUrl.optionally)
    }) as { base: string, optionally: string }[];

    if (search.length && !this.isAlreadyChecked.includes(search[0].base)) {
      let width = `Возможно вы имели ввиду: https://${search[0].base} optionally: /${search[0].optionally}`.length + 460;
      this.isAlreadyChecked.push(search[0].base);
      this.dialog.open(ShowWarningComponent, {
        position: {top: '100px'},
        width: `${width}`,
        data: {base: `Возможно вы имели ввиду: https://${search[0].base}`, optionally: `/${search[0].optionally}`},
        panelClass: 'custom-dialog-container'
      }).afterClosed().subscribe(res => {});
    }
  }

  private isExceptionField(token): number | undefined {
    return this.exceptionFields.filter((field)=> field.name === token)[0]?.type;
  }
}

/*
Click URL
|  https://ads.adfox.ru                            | goLink            |
|  https://ad.adriver.ru                           | cgi-bin/click.cgi?|
|  https://ad.doubleclick.net                | ddm/trackclk     |
|  https://wcm.solution.weborama.fr | fcgi-bin/dispatch.fcgi?a.A=cl|
https://wcm.solution.weborama.fr/fcgi-bin/dispatch.fcgi?a.A=cl


URL Регистрации Показа*
|  https://ads.adfox.ru                            | getCode         |
|  https://ad.adriver.ru                           | cgi-bin/rle.cgi?|
|  https://ad.doubleclick.net                | ddm/trackimp     |
|  https://wcm.solution.weborama.fr | fcgi-bin/dispatch.fcgi?a.A=im|
 */
