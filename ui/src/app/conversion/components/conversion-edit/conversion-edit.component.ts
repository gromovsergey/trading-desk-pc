import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ConversionService} from '../../services/conversion.service';
import {ConversionContainerModel} from '../../models/conversion-container.model';
import {Subscription} from 'rxjs';
import {AdvertiserModel, AdvertiserSessionModel} from '../../../advertiser/models';
import {L10nStatic} from '../../../shared/static/l10n.static';
import {ErrorHelperStatic} from '../../../shared/static/error-helper.static';

@Component({
  selector: 'ui-conversion-edit',
  templateUrl: 'conversion-edit.component.html',
  styleUrls: ['./conversion-edit.component.scss']
})
export class ConversionEditComponent implements OnInit, OnDestroy {

  backUrl: string;
  mode: string;
  routerSubscription: Subscription;
  title: string;
  wait = true;
  waitSubmit = false;
  advertiserSessionModel: AdvertiserSessionModel = new AdvertiserSessionModel();
  conversion: ConversionContainerModel;
  errors: any = {};
  matcher = ErrorHelperStatic.getErrorMatcher;

  constructor(private conversionService: ConversionService,
              private route: ActivatedRoute,
              private router: Router) {

    if (this.mode) {
      this.initTitle();
    }
  }

  ngOnInit(): void {
    this.routerSubscription = this.route.url.subscribe(params => {
      if (params[0].path === 'add') {
        this.mode = 'add';
        this.initTitle();
        this.backUrl = `/advertiser/${this.advertiserSessionModel.id}/conversions`;
        this.conversion = new ConversionContainerModel();
        this.conversion.conversion.impWindow = 30;
        this.conversion.conversion.clickWindow = 30;

        this.wait = false;
      } else {
        this.mode = 'edit';
        this.initTitle();
        this.conversionService.getById(+params[0].path)
          .then(conversion => {
            this.conversion = conversion;
            this.backUrl = `/advertiser/${this.advertiserSessionModel.id}/conversions`;
            this.wait = false;
          });
      }
    });
  }

  ngOnDestroy(): void {
    if (this.routerSubscription) {
      this.routerSubscription.unsubscribe();
    }
  }

  submitForm(): void {
    this.waitSubmit = true;
    const adv = new AdvertiserModel();
    adv.id = this.advertiserSessionModel.id;
    this.conversion.conversion.account = adv;

    let promise;
    if (this.mode === 'add') {
      promise = this.conversionService.create(this.conversion);
    } else {
      promise = this.conversionService.update(this.conversion);
    }

    promise
      .then(() => {
        this.waitSubmit = false;
        this.router.navigateByUrl(this.backUrl);
      })
      .catch(err => {
        if (err.status === 412) {
          this.errors = ErrorHelperStatic.matchErrors(err);
          this.waitSubmit = false;
        }
      });
  }

  private initTitle(): void {
    if (this.mode === 'add') {
      this.title = L10nStatic.translate('advertiserAccount.conversion.addConversion');
    } else {
      this.title = L10nStatic.translate('advertiserAccount.conversion.editConversion');
    }
  }
}
