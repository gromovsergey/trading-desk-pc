import { Component, Input, Output, EventEmitter } from '@angular/core';
import { UserSessionModel }                       from '../user/user_session.model';
import { CreativeOptionComponent }                from './creative_options.component';

@Component({
    selector: 'ui-creative-option-group',
    templateUrl: 'option_group.html'
})

export class CreativeOptionGroupComponent {

    @Input() group: any;
    @Input() errors: any;
    @Output() onChange = new EventEmitter();

    public showGroup: boolean;


    private ngOnInit(){
        if (this.group){
            this.showGroup  = Boolean(this.group.type === 'Advertiser');
            if (!new UserSessionModel().isInternal()){
                let visibleOptions  = this.group.options.filter(f => {
                    return f.internalUse === false;
                });
                this.showGroup  = this.showGroup && visibleOptions && visibleOptions.length;
            }
        } else {
            this.showGroup  = false;
        }
    }


    private onOptionChange(e: any){
        let opt = this.group.options.map(v => {
            return {
                "id":       v.id,
                "token":    v.token || '',
                "value":    v.value || null
            }
        });
        this.onChange.emit(opt);
    }
}
