import {Component, Input, Output, EventEmitter, OnInit} from '@angular/core';
import {UserSessionModel} from '../../../user/models/user-session.model';

@Component({
  selector: 'ui-creative-option-group',
  templateUrl: 'creative-option-group.component.html',
  styleUrls: ['./creative-option-group.component.scss']
})
export class CreativeOptionGroupComponent implements OnInit {
  @Input()
  group: any;

  @Input()
  errors: any;

  @Output()
  optionGroupChange = new EventEmitter();

  showGroup: boolean;

  ngOnInit(): void {
    if (this.group) {
      this.showGroup = Boolean(this.group.type === 'Advertiser');
      if (!new UserSessionModel().isInternal()) {
        const visibleOptions = this.group.options.filter(f => f.internalUse === false);
        this.showGroup = this.showGroup && visibleOptions && visibleOptions.length;
      }
    } else {
      this.showGroup = false;
    }
  }

  optionChange(): void {
    const opt = this.group.options.map(v => ({
        id: v.id,
        token: v.token || '',
        value: v.value || null
      }));
    this.optionGroupChange.emit(opt);
  }
}
