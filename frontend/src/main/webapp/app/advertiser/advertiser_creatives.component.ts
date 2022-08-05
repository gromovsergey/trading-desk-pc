import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';

import {AgencyService} from '../agency/agency.service';
import {CreativeService} from '../creative/creative.service';

import {AdvertiserService} from './advertiser.service';
import {AdvertiserComponent} from './advertiser.component';
import {FileService} from "../shared/file.service";
import {MAX_UPLOAD_FILE_SIZE} from "../common/common.const";
import {CreativeUpload} from "./creative_upload.model";

@Component({
    selector: 'ui-advertiser-creatives',
    templateUrl: 'creatives.html'
})

export class AdvertiserCreativesComponent extends AdvertiserComponent implements OnInit {

    @ViewChild('fileUpload') fileUploadEl: ElementRef;
    public errors;
    public uploadErrors;
    public btnDisable: boolean = false;
    public showCreativeUpload: boolean = false;
    public creativeUploadOpts;
    public creativeUpload: CreativeUpload;
    public imageTemplate;
    public altTextOption;
    public clickUrlOption;
    public landingPageUrlOption;
    public categories;
    public selectedCategories;
    public waitSubmit: boolean = false;

    private sort = function (a, b) {
        if (a.name === b.name) return 0;
        return (a.name > b.name) ? 1 : -1;
    };

    public title: string;
    protected titlePrefix: string;
    private creativeList: Array<any>;
    private creativePreview;
    private waitCreatives = true;

    private wizardDisabled: boolean     = true;
    private wizardTemplates: boolean    = false;
    private wizardSizes: boolean        = false;
    private wizardSizesBlocked: boolean = false;
    private wizardTemplatesOpts;
    private wizardSizesOpts;

    private templateList;
    private template    = null;
    private sizeList;
    private size    = null;
    private canCreateCreative: boolean;
    private canUpdateCreative: boolean;

    constructor(protected advertiserService: AdvertiserService,
                protected agencyService: AgencyService,
                protected fileService: FileService,
                protected route: ActivatedRoute,
                private router: Router,
                private creativeService: CreativeService){
        super(advertiserService, agencyService, fileService, route);
    }

    protected initResources(): void {
        this.title = '_L10N_(advertiserAccount.accountCreatives)';
        this.titlePrefix = '_L10N_(advertiserAccount.accountCreatives)' + ': ';

        this.wizardTemplatesOpts = {
            title:      '_L10N_(creative.chooseTemplate)',
            btnTitle:   '_L10N_(button.next)',
            btnIcon:    'chevron-right',
            btnIconDisabled: true
        };
        this.wizardSizesOpts = {
            title:      '_L10N_(creative.chooseSize)',
            btnTitle:   '_L10N_(button.back)',
            btnIcon:    'chevron-left',
            btnIconDisabled: false
        };
        this.creativeUploadOpts = {
            title:      '_L10N_(creative.upload.title)',
            btnTitle:   '_L10N_(creative.upload.button.create)',
            btnIconDisabled: false
        };
    }

    ngOnInit() {
        this.onInit();

        this.loadCreatives();

        this.promise.then(() => {
            return  this.advertiserService
                        .getTemplates(this.advertiser.id)
                        .then(list => {
                            this.templateList = list
                        });

        }).then(() => {
            this.wizardDisabled = false;
        });
    }

    private loadCreatives() {
        this.waitCreatives = true;
        this.promise.then(data=>{
            return Promise.all([
                this.creativeService.getListByAdvertiserId(this.advertiser.id),
                this.creativeService.isAllowedLocal(this.advertiser.id, 'advertiserEntity.create'),
                this.creativeService.isAllowedLocal(this.advertiser.id, 'advertiserEntity.update')
            ]);
        }).then(res  => {
            let creatives   = res[0];
            creatives.forEach(v => {
                v.creativeId    = v.id;
            });
            this.creativeList = creatives.filter(creative => {
                return creative.displayStatus !== 'DELETED';
            });

            this.canCreateCreative = Boolean(res[1]);
            this.canUpdateCreative = Boolean(res[2]);

            this.waitCreatives = false;
        });
    }

    private preview(e: any, creative: any) {
        e.preventDefault();
        this.creativePreview        = creative;
    }

    private onPreviewClose(e?: any) {
        this.creativePreview        = null;
    }

    private changeStatus(creative: any){
        this.creativeService
            .creativeStatusChange(creative.id, creative.statusChangeOperation)
            .then(newStatus => {
                creative.displayStatus = newStatus.split('|')[0];
            });
    }

    private deleteCreative(e: any, creative: any){
        this.creativeList   = this.creativeList.filter(c => {
            return c.id !== creative.id;
        });
        this.creativeService
            .creativeStatusChange(creative.id, 'DELETE')
            .then(newStatus => {
                creative.displayStatus = newStatus.split('|')[0];
            });
    }

    private showWizardTemplates(e?: any){
        this.size       = null;
        this.wizardTemplates = true;
    }

    private closeWizardTemplates(e?: any){
        this.wizardTemplates = false;
    }

    private showWizardSizes(e?: any){
        this.size = null;
        this.wizardSizes = true;
        this.wizardSizesBlocked = true;
        this.advertiserService.getSizes(this.template.id, this.advertiser.id)
            .then(list  => {
                this.sizeList   = list;
                this.wizardSizesBlocked = false;
            })
            .catch(e => {
                alert(e);
                this.wizardSizesBlocked = false;
            });
    }

    private closeWizardSizes(e?: any){
        this.wizardSizes = false;
    }

    private openWizardNext(e?: any){
        this.closeWizardTemplates();
        this.showWizardSizes();
    }

    private selectTemplate(e: any, template: any){
        e.preventDefault();
        e.stopPropagation();

        this.template               = template;
        this.wizardTemplatesOpts    = Object.assign({}, this.wizardTemplatesOpts, {btnIconDisabled: false});
        this.openWizardNext();
    }

    private selectSize(e: any, size: any){
        e.preventDefault();
        e.stopPropagation();

        this.size   = size;
        this.wizardSizesOpts    = Object.assign({}, this.wizardSizesOpts, {btnIconDisabled: false});
        this.redirectEditPage();
    }

    private redirectEditPage(e?: any){
        if (this.template && this.size){
            this.router.navigateByUrl(`/advertiser/${this.advertiser.id}/creative/new/template/${this.template.id}/size/${this.size.id}`);
        }
    }

    private wizardSizeBack(e?: any){
        this.closeWizardSizes();
        this.showWizardTemplates();
    }

    public selectFile(e: any){
        e.preventDefault();
        this.fileUploadEl.nativeElement.click();
    }

    public uploadZip(e: any) {
        let el = this.fileUploadEl.nativeElement;
        this.errors = [];
        this.uploadErrors = null;

        if (el.files.length === 0) {
            return;
        }

        if (el.files[0]['size'] > MAX_UPLOAD_FILE_SIZE) {
            this.errors.push('File size should not exceed ' + Math.round(MAX_UPLOAD_FILE_SIZE / (1024 * 1024)) + 'Mb');
            return;
        }

        if (el.files.length > 1) {
            this.errors.push('Can\'t upload several files. Only the first one will be uploaded');
            return;
        }

        let formData = new FormData();
        formData.append('file', el.files[0]);

        this.wait = true;
        this.btnDisable = true;

        this.fileService.checkFileExist(formData, this.advertiser.id)
            .then(res => {
                if (res && !confirm('_L10N_(creative.upload.fileExistConfirm)')) {
                    this.wait = false;
                    this.btnEnable();
                } else {
                    this.advertiserService.getImageTemplate(this.advertiser.id).then(res => {
                        this.imageTemplate = res;
                        if (this.imageTemplate.id == undefined) {
                            this.errors.push('No image template to create creatives');
                            this.wait = false;
                            this.btnEnable();
                            return;
                        }

                        for (let group of this.imageTemplate.optionGroups){
                            for (let opt of group.options){
                                if (opt.token === 'ALTTEXT'){
                                    this.altTextOption = opt;
                                }
                                if (opt.token === 'CRCLICK'){
                                    this.clickUrlOption = opt;
                                }
                                if (opt.token === 'DESTURL'){
                                    this.landingPageUrlOption = opt;
                                }
                            }
                        }
                        if (this.altTextOption == undefined || this.clickUrlOption == undefined || this.landingPageUrlOption == undefined) {
                            this.errors.push('No options to create creatives');
                            this.wait = false;
                            this.btnEnable();
                            return;
                        }

                        Promise.all([
                            this.fileService.uploadCreativesZip(formData, this.advertiser.id),
                            this.advertiserService.getSizes(this.imageTemplate.id, this.advertiser.id),
                            this.creativeService.getCategories(),
                            this.creativeService.getContentCategories(this.advertiser.id)
                        ]).then(res => {
                            this.creativeUpload = new CreativeUpload();
                            this.creativeUpload.accountId = this.advertiser.id;
                            for (let image of res[0]) {
                                this.creativeUpload.imagesList.push(image);
                            }

                            for (let image of this.creativeUpload.imagesList) {
                                for (let size of res[1]) {
                                    if (size.width == image.dimensions.width && size.height == image.dimensions.height) {
                                        image.sizeExist = true;
                                        break;
                                    }
                                }
                            }

                            this.categories = res[2];
                            this.selectedCategories = res[3];

                            this.showCreativeUpload = true;

                            this.wait = false;
                            this.btnEnable();

                        }).catch(e => {
                            this.wait = false;
                            this.btnEnable();
                            this.errors = e && e.json ? e.json()['actionError'] : [];
                        });

                    }).catch(e => {
                        this.wait = false;
                        this.btnEnable();
                        this.errors = e && e.json ? e.json()['actionError'] : [];
                    });
                }
            })
            .catch(e => {
                this.wait = false;
                this.btnEnable();
                this.errors = e && e.json ? e.json()['actionError'] : [];
            });
    }

    private setContentCategories(e: any){
        this.selectedCategories = e;
    }

    protected btnEnable(e?: any){
        this.btnDisable = false;
    }

    private closeCreativeUpload(e?: any){
        this.showCreativeUpload = false;
    }

    private toggleCheckedAll(e) {
        this.creativeUpload.imagesList.forEach(v => {
            if (v.sizeExist) {
                v.checked = e.target.checked;
            }
        });
    }

    private uploadCreatives(e?: any) {
        this.waitSubmit = true;
        this.uploadErrors = null;

        let upload = Object.assign({}, this.creativeUpload);

        for (let category of this.selectedCategories) {
            upload.categories.push(category.id);
        }

        upload.imagesList = upload.imagesList.filter(p => p.checked);

        this.advertiserService.uploadCreatives(upload)
            .then(v => {
                this.waitSubmit = false;
                this.showCreativeUpload = false;
                this.loadCreatives();
            })
            .catch(e => {
                this.uploadErrors = e && e.json ? e.json() : [];
                this.waitSubmit = false;
            });
    }
}
