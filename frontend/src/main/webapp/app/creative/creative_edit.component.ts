import { Component, OnInit, OnDestroy }         from '@angular/core';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { Subscription }                         from 'rxjs/Rx';

import { LoadingComponent }             from '../shared/loading.component';
import { IconComponent }                from '../shared/icon.component';
import { IconRequiredComponent }        from '../shared/required_icon.component';
import { PanelComponent }               from '../shared/panel.component';
import { OptionTransferComponent }      from '../shared/option_transfer.component';
import { DisplayStatusDirective }       from '../shared/display_status.directive';
import { AdvertiserService }            from '../advertiser/advertiser.service';
import { AdvertiserComponent }          from '../advertiser/advertiser.component';
import { AgencyService }                from '../agency/agency.service';
import { CreativeService }              from './creative.service';
import { Creative }                     from './creative';
import { CreativeModel }                from './creative.model';
import { CreativeOptionGroupComponent } from './creative_option_group.component';
import { CreativeLivePreview }          from './creative_live_preview.component';
import {FileService} from "../shared/file.service";

@Component({
    selector: 'ui-creative-edit',
    templateUrl: 'edit.html'
})

export class CreativeEditComponent extends AdvertiserComponent implements OnInit, OnDestroy {

    public title:string;

    public creative: Creative;
    private creativeRouterSubscription: Subscription;
    private mode: string;
    public mainWait: boolean = true;
    private waitSubmit: boolean = false;
    private backUrl: string;
    private categories;
    private templateStats;
    private templateStatsVisible: boolean;
    private sizeStats;
    private sizeStatsVisible: boolean;
    private rnd: number;
    private errors;
    private accountContentCategories: Array<any>    = [];

    private sort  = function (a, b) {
        if (a.name === b.name) return 0;
        return (a.name > b.name) ? 1 : -1;
    };


    constructor(protected advertiserService: AdvertiserService,
                protected agencyService: AgencyService,
                protected fileService: FileService,
                protected route: ActivatedRoute,
                protected router: Router,
                private creativeService: CreativeService){
        super(advertiserService, agencyService, fileService, route);
    }

    protected initResources(): void {
        this.title = '_L10N_(creative.object)';

        if (this.mode) {
            this.initTitle();
        }
    }

    private initTitle(): void {
        if (this.mode === 'edit') {
            this.title = '_L10N_(creative.editCreative)' + ': ';
            if (this.creative) {
                this.title += this.creative.name;
            }
        } else {
            this.title = '_L10N_(creative.addNewCreative)';
        }
    }

    ngOnInit() {
        this.onInit();

        this.resetErrors();

        this.promise    = this.promise.then(() => {
            return this.creativeService.getCategories()
                .then(list => {
                    this.categories = list;
                });
        });

        this.creativeRouterSubscription   = this.route.params.subscribe(params => {
            if (params['creativeId']) {
                this.mode   = 'edit';
                this.promise = this.promise.then(() => {
                    return this.creativeService
                        .getCreative(+params['creativeId'])
                        .then(creative => {
                            this.creative   = creative;
                            this.initTitle();
                        });
                });
            } else {
                this.mode       = 'add';
                this.promise = this.promise.then(() => {
                    this.initTitle();
                    this.creative   = new CreativeModel();
                    this.creative.templateId    = +params['templateId'];
                    this.creative.sizeId        = +params['sizeId'];
                    this.creative.accountId     = this.advertiser.id;
                    this.creative.agencyId      = this.advertiser.agencyId;

                });
            }

            this.promise.then(() => {
                this.creativeService.getOptions(this.creative.accountId, this.creative.sizeId, this.creative.templateId)
                    .then(options => {
                        this.onStatsLoad(options);
                        this.mainWait    = false;
                    });
            });

            this.backUrl    = '/advertiser/'+params['id']+'/creatives';
        });
    }

    ngOnDestroy(){
        if (this.creativeRouterSubscription){
            this.creativeRouterSubscription.unsubscribe();
        }
    }

    private doSubmit(e: any){
        e.preventDefault();
        if (this.waitSubmit) return;

        let postMethod  = this.mode === 'add' ? 'createCreative' : 'updateCreative';

        this.waitSubmit = true;
        this.resetErrors();
        this.creativeService[postMethod](this.creative)
            .then(v => {
                this.waitSubmit = false;
                this.router.navigateByUrl(this.backUrl);
            })
            .catch(e => {
                if (e.status    === 412){
                    this.resetErrors(e.json());
                    this.waitSubmit = false;

                    setImmediate(()=>{
                        let errorEl = window.document.querySelector('.has-error');
                        if (errorEl !== null){
                            let bounds  = errorEl.getBoundingClientRect();
                            window.scrollTo(0, window.scrollY+bounds.top-70);
                        }
                    });
                }
            });
    }

    private resetErrors(errors?: any){
        this.errors = Object.assign({
            actionError: null,
            name: null,
            options: null,
            template: null,
            size: null
        }, errors || {});
    }

    private setVisualCategories(e: any){
        this.creative.visualCategories  = e;
    }

    private setContentCategories(e: any){
        this.creative.contentCategories = e;
    }

    private onStatsLoad(options){
        this.sizeStats      = options.size || null;
        this.templateStats  = options.template || null;
        this.accountContentCategories   = options.accountContentCategories || null;

        this.sizeStatsVisible       = this.sizeStats.optionGroups.find(v => {return v.type === 'Advertiser'}) !== undefined;
        this.templateStatsVisible   = this.templateStats.optionGroups.find(v => {return v.type === 'Advertiser'}) !== undefined;

        this.provideDefaultVals(this.sizeStats);
        this.provideDefaultVals(this.templateStats);

        if (this.mode === 'add'){
            this.creative.height    = this.sizeStats.height;
            this.creative.width     = this.sizeStats.width;

            this.creative.contentCategories = Object.assign([], this.accountContentCategories);
        } else { // edit
            this.provideOptionVals(this.creative.options);
        }
    }

    private provideDefaultVals(stats){
        stats.optionGroups.forEach(group => {
            group.options.forEach(option => {
                if (option.defaultValue && !option.value) {
                    option.value = option.defaultValue;
                }
            });
        });
    }

    provideOptionVals(options){
        let foundInStats = (option, stats) => {
            for (let group of stats.optionGroups){
                for (let opt of group.options){
                    if (opt.id === option.id){
                        return opt;
                    }
                }
            }
        };

        options.forEach(option => {
            let found = foundInStats(option, this.templateStats);
            if (found) {
                found.value = option.value;
            } else if (found = foundInStats(option, this.sizeStats)){
                found.value = option.value;
            }
        });
    }

    private toggleExpandable(e: any){
        e.preventDefault();
        this.creative.expandable    = !this.creative.expandable;
        if (!this.creative.expandable) {
            this.creative.expansion = null;
        } else {
            this.creative.expansion = this.sizeStats.expansions[0] || null;
        }
    }

    private updateLivePreview(){
        this.rnd    = Math.random();
    }

    private onOptionGroupChange(groupOptions: any){
        if (groupOptions){
            groupOptions.forEach(gOpt => {
                let found = this.creative.options.find(f => {
                    return f.id === gOpt.id && f.token === gOpt.token;
                });
                if (found){
                    found.value = gOpt.value;
                } else {
                    this.creative.options.push(gOpt);
                }
            });
        }
        setImmediate(() => {
            this.updateLivePreview();
        });
    }
}
