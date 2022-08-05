import {ChangeDetectorRef, Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {AgencyService} from '../../../agency/services/agency.service';
import {CreativeService} from '../../../creative/services/creative.service';
import {AdvertiserService} from '../../services/advertiser.service';
import {FileService} from '../../../shared/services/file.service';
import {MAX_UPLOAD_FILE_SIZE} from '../../../common/common.const';
import {AdvertiserSessionModel} from '../../models';
import {AdvertiserComponent} from '../advertiser/advertiser.component';
import {ErrorHelperStatic} from '../../../shared/static/error-helper.static';
import {L10nStatic} from '../../../shared/static/l10n.static';
import {MatDialog} from '@angular/material/dialog';
import {CreativePreviewComponent} from '../../../creative/components/creative-preview/creative-preview.component';
import {ArrayHelperStatic} from '../../../shared/static/array-helper.static';
import {CreativeWizardComponent} from '../../../creative/components/creative-wizard/creative-wizard.component';
import * as JSZip from "jszip";
import {UploadFileComponent} from "./uploadFile/upload-file.component";

interface IUploadOptions {
  title: string,
  btnTitle: string,
  btnIconDisabled: boolean
}

interface ICreative {
  accountId?: number;
  agencyId?: number;
  creativeId?: number;
  displayStatus?: string;
  height?: string | number;
  id?: number;
  name?: string;
  sizeId?: number;
  sizeName?: string;
  templateId?: number;
  templateName?: string;
  width?: string | number;
}

@Component({
  selector: 'ui-advertiser-creatives',
  templateUrl: './advertiser-creatives.component.html',
  styleUrls: ['./advertiser-creatives.component.scss']
})
export class AdvertiserCreativesComponent extends AdvertiserComponent implements OnInit, OnDestroy {

  @ViewChild('fileUpload') fileUploadEl: ElementRef;
  public errors;
  public uploadErrors;
  public btnDisable: boolean;
  public showCreativeUpload: boolean;
  public creativeUploadOpts: IUploadOptions;
  public waitSubmit: boolean;
  public categories;
  public creativeList: ICreative[];
  public waitCreatives: boolean;
  public canCreateCreative: boolean;
  public canUpdateCreative: boolean;
  public sort: ArrayHelperStatic;
  public isHaveSubfolder: boolean;
  readonly displayedColumns = ['name', 'size', 'template', 'action'];

  constructor(protected advertiserService: AdvertiserService,
              protected agencyService: AgencyService,
              protected fileService: FileService,
              protected route: ActivatedRoute,
              protected router: Router,
              protected creativeService: CreativeService,
              protected dialog: MatDialog) {
    super(advertiserService, agencyService, fileService, route, dialog);
    this.btnDisable = false;
    this.showCreativeUpload = false;
    this.waitSubmit = false;
    this.waitCreatives = true;
    this.sort = ArrayHelperStatic.sortByKey.bind(null, 'name');
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.advertiser = new AdvertiserSessionModel().data;
    this.loadCreatives().then(() => {
      this.initResources();
    });
  }

  ngOnDestroy() {
  }

  async loadCreatives(): Promise<any> {
    this.waitCreatives = true;

    try {
      const res = await Promise.all([
        this.creativeService.getListByAdvertiserId(this.advertiser.id),
        this.creativeService.isAllowedLocal(this.advertiser.id, 'advertiserEntity.create'),
        this.creativeService.isAllowedLocal(this.advertiser.id, 'advertiserEntity.update')
      ]);

      const creatives = res[0];
      creatives.forEach(v => {
        v.creativeId = v.id;
      });
      this.creativeList = creatives.filter(creative => creative.displayStatus !== 'DELETED');

      this.canCreateCreative = Boolean(res[1]);
      this.canUpdateCreative = Boolean(res[2]);
    } catch (err) {
    } finally {
      this.waitCreatives = false;
    }
  }

  preview(creative: any): void {
    this.dialog.open(CreativePreviewComponent, {
      data: {creative},
    });
  }

  changeStatus(creative: any): void {
    this.creativeService
      .creativeStatusChange(creative.id, creative.statusChangeOperation)
      .then(newStatus => {
        creative.displayStatus = newStatus.split('|')[0];
      });
  }

  deleteCreative(creative: any): void {
    this.creativeList = this.creativeList.filter(c => c.id !== creative.id);
    this.creativeService
      .creativeStatusChange(creative.id, 'DELETE')
      .then(newStatus => {
        creative.displayStatus = newStatus.split('|')[0];
      });
  }

  copyCreative(creative: any): void {
    this.wait = true;
    this.creativeService.copy(creative.id).then(() => {
      this.wait = false;
      this.loadCreatives();
    });
  }

  showWizardTemplates(): void {
    this.dialog.open(CreativeWizardComponent, {
      minWidth: 600,
      minHeight: 400
    });
  }

  selectFile(e: any): void {
    e.preventDefault();
    this.fileUploadEl.nativeElement.click();
  }

  handlerError(err){
    this.wait = false;
    this.btnEnable();
    this.errors = ErrorHelperStatic.matchErrors(err) || [];
  }

  public uploadZip(): void {
    const el = this.fileUploadEl.nativeElement;

    let reader = new FileReader();
    let zip = new JSZip();
    reader.readAsArrayBuffer(el.files[0]);
    new Promise((resolve) => {
      reader.onload = () => {
        zip.loadAsync(reader.result, {base64: true}).then(data => {
          this.isHaveSubfolder = Object.values(data.files).some(file => file.name.includes('/'));
          resolve(true);
        });
      };
    }).then((data) => {
      if (data) {
        this.errors = [];
        this.uploadErrors = null;
        if (el.files.length === 0) {
          return;
        }

        if (el.files[0].size > MAX_UPLOAD_FILE_SIZE) {
          this.errors.push('File size should not exceed ' + Math.round(MAX_UPLOAD_FILE_SIZE / (1024 * 1024)) + 'Mb');
          return;
        }

        if (el.files.length > 1) {
          this.errors.push('Can\'t upload several files. Only the first one will be uploaded.');
          return;
        }

        if (this.isHaveSubfolder) {
          this.errors.push('Please check the contents of the archive, it should not have subfolders and the name of the image should not contain the "/" character.');
          return;
        }
        const formData = new FormData();
        formData.append('file', el.files[0]);

        this.wait = true;
        this.btnDisable = true;

        this.fileService.checkFileExist$(formData, this.advertiser.id).subscribe({
          next: (res) => {
            this.dialog.open(UploadFileComponent, {
              data: {
                res,
                advertiser: this.advertiser,
                formData
              },
              minWidth: 360,
            }).afterClosed().subscribe(result => {
              this.wait = false;
              this.btnDisable = false;
              this.loadCreatives();
            });
          },
          error: () => {this.handlerError.bind(this);},
          complete: () => {}
        });
      }
    });
  }

  public btnEnable(): void {
    this.btnDisable = false;
  }

  protected initResources(): void {
    this.creativeUploadOpts = {
      title: L10nStatic.translate('creative.upload.title'),
      btnTitle: L10nStatic.translate('creative.upload.button.create'),
      btnIconDisabled: false
    };
  }
}
