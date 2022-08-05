import {Component, OnInit, OnDestroy} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Subscription} from 'rxjs';
import {AdvertiserService} from '../../../advertiser/services/advertiser.service';
import {AdvertiserComponent} from '../../../advertiser/components/advertiser/advertiser.component';
import {AgencyService} from '../../../agency/services/agency.service';
import {CreativeService} from '../../services/creative.service';
import {CreativeModel} from '../../models/creative.model';
import {FileService} from '../../../shared/services/file.service';
import {ErrorHelperStatic} from '../../../shared/static/error-helper.static';
import {ArrayHelperStatic} from '../../../shared/static/array-helper.static';
import {MatDialog} from '@angular/material/dialog';

@Component({
  selector: 'ui-creative-edit',
  templateUrl: 'creative-edit.component.html',
  styleUrls: ['./creative-edit.component.scss']
})
export class CreativeEditComponent extends AdvertiserComponent implements OnInit, OnDestroy {

  title: string;
  creative: Creative;
  mode: string;
  mainWait = true;
  waitSubmit = false;
  backUrl: string[];
  categories;
  templateStats;
  templateStatsVisible: boolean;
  sizeStats;
  sizeStatsVisible: boolean;
  rnd: number;
  errors;
  matcher = ErrorHelperStatic.getErrorMatcher;
  accountContentCategories: any[] = [];
  sort = ArrayHelperStatic.sortByKey.bind(this, 'name');
  private creativeRouterSubscription: Subscription;

  constructor(protected advertiserService: AdvertiserService,
              protected agencyService: AgencyService,
              protected fileService: FileService,
              protected route: ActivatedRoute,
              protected router: Router,
              private creativeService: CreativeService,
              protected dialog: MatDialog) {
    super(advertiserService, agencyService, fileService, route, dialog);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.resetErrors();
    let promise = this.initComponent();

    this.creativeRouterSubscription = this.route.paramMap.subscribe((params) => {
      const creativeId = +params.get('creativeId');
      if (creativeId) {
        this.mode = 'edit';
        promise = promise.then(() => this.creativeService
            .getCreative(creativeId)
            .then(creative => {
              this.creative = creative;
            }));
      } else {
        this.mode = 'add';
        promise = Promise.all([promise, this.promise]).then(() => {
          this.creative = new CreativeModel();
          this.creative.templateId = +params.get('templateId');
          this.creative.sizeId = +params.get('sizeId');
          this.creative.accountId = +params.get('id');
          this.creative.agencyId = this.advertiser.agencyId;
        });
      }

      promise.then(() => {
        this.creativeService.getOptions(this.creative.accountId, this.creative.sizeId, this.creative.templateId)
          .then(options => {
            this.onStatsLoad(options);
            this.mainWait = false;
          });
      });

      this.backUrl = ['/advertiser', params.get('id'), 'creatives'];
    });
  }

  ngOnDestroy(): void {
    if (this.creativeRouterSubscription) {
      this.creativeRouterSubscription.unsubscribe();
    }
  }

  async initComponent(): Promise<any> {
    try {
      this.categories = await this.creativeService.getCategories();
    } catch (e) {
      console.error(e);
    }
  }

  doSubmit(e: any): void {
    e.preventDefault();
    if (this.waitSubmit) {
      return;
    }

    const postMethod = this.mode === 'add' ? 'createCreative' : 'updateCreative';

    this.waitSubmit = true;
    this.resetErrors();
    this.creativeService[postMethod](this.creative)
      .then(() => {
        this.waitSubmit = false;
        this.router.navigate(this.backUrl);
      })
      .catch(err => {
        if (err.status === 412) {
          this.resetErrors(ErrorHelperStatic.matchErrors(err));
          this.waitSubmit = false;
        }
      });
  }

  resetErrors(errors?: any): void {
    this.errors = Object.assign({
      actionError: null,
      name: null,
      options: null,
      template: null,
      size: null
    }, errors || {});
  }

  setVisualCategories(e: any): void {
    this.creative.visualCategories = e;
  }

  setContentCategories(e: any): void {
    this.creative.contentCategories = e;
  }

  onStatsLoad(options): void {
    this.sizeStats = options.size || null;
    this.templateStats = options.template || null;
    this.accountContentCategories = options.accountContentCategories || null;

    this.sizeStatsVisible = this.sizeStats.optionGroups.find(v => v.type === 'Advertiser') !== undefined;
    this.templateStatsVisible = this.templateStats.optionGroups.find(v => v.type === 'Advertiser') !== undefined;

    this.provideDefaultVals(this.sizeStats);
    this.provideDefaultVals(this.templateStats);

    if (this.mode === 'add') {
      this.creative.height = this.sizeStats.height;
      this.creative.width = this.sizeStats.width;

      this.creative.contentCategories = Object.assign([], this.accountContentCategories);
    } else { // edit
      this.provideOptionVals(this.creative.options);
    }
  }

  provideDefaultVals(stats): void {
    stats.optionGroups.forEach(group => {
      group.options.forEach(option => {
        if (option.defaultValue && !option.value) {
          option.value = option.defaultValue;
        }
      });
    });
  }

  provideOptionVals(options): void {
    const foundInStats = (option, stats) => {
      for (const group of stats.optionGroups) {
        for (const opt of group.options) {
          if (opt.id === option.id) {
            return opt;
          }
        }
      }
    };

    options.forEach(option => {
      let found = foundInStats(option, this.templateStats);
      if (found) {
        found.value = option.value;
      } else {
        found = foundInStats(option, this.sizeStats);
        if (found) {
          found.value = option.value;
        }
      }
    });
  }

/*  toggleExpandable(e: any): void {
    e.preventDefault();
    this.creative.expandable = !this.creative.expandable;
    if (!this.creative.expandable) {
      this.creative.expansion = null;
    } else {
      this.creative.expansion = this.sizeStats.expansions[0] || null;
    }
  }*/

  updateLivePreview(): void {
    this.rnd = Math.random();
  }

  onOptionGroupChange(groupOptions: any): void {
    if (groupOptions) {
      groupOptions.forEach(gOpt => {
        const found = this.creative.options.find(f => f.id === gOpt.id && f.token === gOpt.token);
        if (found) {
          found.value = gOpt.value;
        } else {
          this.creative.options.push(gOpt);
        }
      });
    }
    window.setTimeout(() => {
      this.updateLivePreview();
    });
  }
}
