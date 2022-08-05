import {
  Component,
  OnInit,
  Inject
} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";
import {AdvertiserService} from "../../../services/advertiser.service";
import {L10nStatic} from "../../../../shared/static/l10n.static";
import {FileService} from "../../../../shared/services/file.service";
import {CreativeService} from "../../../../creative/services/creative.service";
import {CreativeUpload} from "../../../models";
import {ArrayHelperStatic} from "../../../../shared/static/array-helper.static";
import {ErrorHelperStatic} from "../../../../shared/static/error-helper.static";
import {
  ShowWarningComponent
} from "../../../../creative/components/creative-options/show-warning/show-warning.component";

@Component({
  selector: 'ui-channel-tree',
  templateUrl: './upload-file.component.html',
  styleUrls: ['./upload-file.component.scss']
})
export class UploadFileComponent implements OnInit {

  // @ViewChild('textInput') textInputEl: ElementRef;
  //
  // @Input() sources: string[] = [];
  // @Input() accountId: number;
  // @Input() selectedChannelsInput: IdName[] = [];
  //
  // @Output() save: EventEmitter<IdName[]> = new EventEmitter<IdName[]>();
  //
  // @ContentChild('customContent') customContentEl: ElementRef;
  // @ViewChild('customContent') customContentChildEl: ElementRef;

  uploadErrors;
  errors;
  res;
  advertiser;
  imageTemplate;
  altTextOption;
  clickUrlOption;
  landingPageUrlOption;
  crAdvTrackPixelOption;
  crAdvTrackPixel2Option;


  showCreativeUpload = false;
  categories;
  selectedCategories;

  waitSubmit: boolean;
  creativeUpload: CreativeUpload;
  formData;
  sort = ArrayHelperStatic.sortByKey.bind(null, 'name');
  exceptionUrl: { base: string, optionally: string }[];
  isAlreadyChecked: string[];
  constructor(
    protected advertiserService: AdvertiserService,
    protected fileService: FileService,
    protected creativeService: CreativeService,
    public dialog: MatDialog,
    private dialogRef: MatDialogRef<UploadFileComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {
      res: any,
      advertiser: any,
      formData: any,
      // channelsLink: [],
      // setChannelsLink: (arg0: any, arg1: number, arg2: boolean) => void
    }
  ) {
    this.exceptionUrl = [
      { base: 'ads.adfox.ru', optionally: 'goLink' },
      { base: 'ad.adriver.ru', optionally: 'cgi-bin/click.cgi?' },
      { base: 'ad.doubleclick.net', optionally: 'ddm/trackclk' },
      { base: 'wcm.solution.weborama.fr', optionally: 'fcgi-bin/dispatch.fcgi?a.A=cl' }
    ];
    this.isAlreadyChecked = [];
  }

  ngOnInit(): void {
    this.res = this.data.res
    this.advertiser = this.data.advertiser
    this.formData = this.data.formData

    if (this.res && !confirm(L10nStatic.translate('creative.upload.fileExistConfirm'))) {
    } else {
      this.advertiserService.getImageTemplate(this.advertiser.id).then(res2 => {
        this.imageTemplate = res2;
        if (this.imageTemplate.id === undefined) {
          this.errors.push('No image template to create creatives');
          return;
        }

        for (const group of this.imageTemplate.optionGroups) {
          for (const opt of group.options) {
            if (opt.token === 'ALTTEXT') {
              this.altTextOption = opt;
            }
            if (opt.token === 'CRCLICK') {
              this.clickUrlOption = opt;
            }
            if (opt.token === 'DESTURL') {
              this.landingPageUrlOption = opt;
            }
            if (opt.token === 'CRADVTRACKPIXEL') {
              this.crAdvTrackPixelOption = opt;
            }
            if (opt.token === 'CRADVTRACKPIXEL2') {
              this.crAdvTrackPixel2Option = opt;
            }
          }
        }
        if (this.altTextOption === undefined ||
          this.clickUrlOption === undefined ||
          this.landingPageUrlOption === undefined
        ) {
          this.errors.push('No options to create creatives');
          return;
        }

        Promise.all([
          this.fileService.uploadCreativesZip(this.formData, this.advertiser.id),
          this.advertiserService.getSizes(this.imageTemplate.id, this.advertiser.id),
          this.creativeService.getCategories(),
          this.creativeService.getContentCategories(this.advertiser.id)
        ]).then(result => {
          this.creativeUpload = new CreativeUpload();
          this.creativeUpload.accountId = this.advertiser.id;
          for (const image of result[0]) {
            this.creativeUpload.imagesList.push(image);
          }

          for (const image of this.creativeUpload.imagesList) {
            for (const size of result[1]) {
              if (size.width === image.dimensions.width && size.height === image.dimensions.height) {
                image.sizeExist = true;
                break;
              }
            }
          }
          this.categories = result[2];
          this.selectedCategories = result[3];
          this.showCreativeUpload = true;

        }).catch(err=> {
          this.uploadErrors = ErrorHelperStatic.matchErrors(err) || { actionError : ['Internal Server Error']};
          this.waitSubmit = false;
        });
      }).catch(err=> console.log(err));
    }
  }

  uploadCreatives(e?: any): void {
    e.preventDefault();
    this.waitSubmit = true
    this.uploadErrors = null;

    const upload = Object.assign({}, this.creativeUpload);

    for (const category of this.selectedCategories) {
      upload.categories.push(category.id);
    }

    upload.imagesList = upload.imagesList.filter(p => p.checked);
    this.advertiserService.uploadCreatives(upload)
      .then(() => {
        this.waitSubmit = false;
        this.showCreativeUpload = false;
        this.dialogRef.close()
      })
      .catch(err => {
        this.uploadErrors = ErrorHelperStatic.matchErrors(err) || [];
        this.waitSubmit = false;
      });
  }


  toggleCheckedAll(e): void {
    this.creativeUpload.imagesList.forEach(v => {
      if (v.sizeExist) {
        v.checked = e.target.checked;
      }
    });
  }
  setContentCategories(e: any): void {
    this.selectedCategories = e;
  }

  checkUrl(): void {
    let clickUrl = this.creativeUpload.clickUrl;
    let search = this.exceptionUrl.filter(currentUrl => {
      return clickUrl.includes(currentUrl.base) && !clickUrl.includes(currentUrl.optionally)
    }) as { base: string, optionally: string }[];

    if (search.length && !this.isAlreadyChecked.includes(search[0].base)) {
      let width = `Возможно вы имели ввиду: https://${search[0].base} /${search[0].optionally}`.length + 472;
      this.isAlreadyChecked.push(search[0].base);
      this.dialog.open(ShowWarningComponent, {
        position: {top: '100px'},
        width: `${width}`,
        data: {base: `Возможно вы имели ввиду: https://${search[0].base}`, optionally: `/${search[0].optionally}`},
        panelClass: 'custom-dialog-container'
      }).afterClosed().subscribe(res => {});
    }
  }
}
