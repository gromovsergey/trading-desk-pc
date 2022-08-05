import { Component, Input, ViewChild, ElementRef, Output, EventEmitter } from '@angular/core';

import { HintComponent }          from "../shared/hint.component";
import { IconRequiredComponent }  from "../shared/required_icon.component";
import { FileUploadComponent }    from "../shared/file_upload.component";
import { jQuery }                 from "../common/common.const";
import { AdvertiserSessionModel } from "../advertiser/advertiser_session.model";
import { UserSessionModel }       from "../user/user_session.model";

@Component({
    selector: 'ui-creative-option',
    templateUrl: 'options.html'
})

export class CreativeOptionComponent {

    @Input() option: any;
    @Input() errors: Array<string>;
    @Output() onChange  = new EventEmitter();
    @ViewChild('option') optionEl: ElementRef;

    private accountId: number   = new AdvertiserSessionModel().id;
    public showOption: boolean;

    constructor() {}

    ngOnInit(){
        this.showOption = new UserSessionModel().isInternal() || !this.option.internalUse;

        if (this.option.defaultValue){
            this.changed();
        }
    }

    ngAfterViewInit(){
        if (this.option.type === 'COLOR'){
            let el = this.optionEl.nativeElement;

            jQuery(el).colorpicker({
                color: this.option.value,
                format: 'hex'
            }).on('changeColor', (e) => {
                this.option.value   = e.color.toHex().substr(1).toUpperCase();
                this.changed();
            });
        }
    }

    ngOnDestroy(){
        if (this.option.type === 'COLOR'){
            let el = this.optionEl.nativeElement;

            jQuery(el).off('changeColor');
        }
    }

    private changed(e?: any){
        this.onChange.emit(this.option);
    }

    private fileUploaded(e: any){
        this.option.value   = e;
        this.changed();
    }
}
