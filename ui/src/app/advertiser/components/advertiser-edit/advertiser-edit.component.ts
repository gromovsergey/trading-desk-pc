import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Subscription} from 'rxjs';
import {AgencySessionModel} from '../../../agency/models/agency-session.model';
import {AdvertiserService} from '../../services/advertiser.service';
import {CommonService} from '../../../common/services/common.service';
import {AdvertiserModel} from '../../models';
import {ErrorHelperStatic} from '../../../shared/static/error-helper.static';

@Component({
  selector: 'ui-advertiser-edit',
  templateUrl: './advertiser-edit.component.html',
  styleUrls: ['./advertiser-edit.component.scss']
})
export class AdvertiserEditComponent implements OnInit, OnDestroy {

  mode: string;
  title: string;
  wait = true;
  waitSubmit = false;
  errors: any;
  matcher = ErrorHelperStatic.getErrorMatcher;
  advertiser: AdvertiserModel = new AdvertiserModel();
  canEditCommission: boolean;
  private routerSubscription: Subscription;
  private agencySessionModel: AgencySessionModel = new AgencySessionModel();

  get backUrl(): (string | number)[] {
    return this.mode === 'add' ? ['/agency', this.agencySessionModel.id, 'advertisers'] :
      ['/advertiser', this.advertiser.id, 'account'];
  }

  constructor(private advertiserService: AdvertiserService,
              private commonService: CommonService,
              private route: ActivatedRoute,
              private router: Router) {
  }

  ngOnInit(): void {
    this.routerSubscription = this.route.url.subscribe(params => {
      if (params[0].path === 'add') {
        this.mode = 'add';

        this.advertiser.commission = 0;
        this.commonService.isAllowedLocal0('account.viewAdvertisingFinance')
          .then(res => {
            this.canEditCommission = res;
            this.wait = false;
          });

      } else {
        this.mode = 'edit';
        this.advertiserService.getById(+params[0].path).toPromise()
          .then(advertiser => {
            this.advertiser = advertiser;
            this.advertiserService.isAllowedLocal(this.advertiser.id, 'account.viewAdvertisingFinance')
              .then(res => {
                this.canEditCommission = res && this.advertiser.financialFieldsFlag && this.advertiser.selfServiceFlag;
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

  submitForm(): void {
    this.waitSubmit = true;
    this.advertiser.agencyId = this.agencySessionModel.id;

    let promise;
    if (this.mode === 'add') {
      promise = this.advertiserService.create(this.advertiser);
    } else {
      promise = this.advertiserService.update(this.advertiser);
    }

    promise
      .then(() => {
        this.waitSubmit = false;
        this.router.navigate(this.backUrl).catch();
      })
      .catch(err => {
        this.errors = ErrorHelperStatic.matchErrors(err);
        this.waitSubmit = false;
      });
  }
}
