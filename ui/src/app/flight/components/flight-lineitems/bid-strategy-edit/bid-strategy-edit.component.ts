import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {L10nStatic} from "../../../../shared/static/l10n.static";
import {FormControl, Validators} from '@angular/forms';

@Component({
  selector: 'ui-bid-strategy-edit',
  templateUrl: './bid-strategy-edit.component.html',
  styleUrls: ['./bid-strategy-edit.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class BidStrategyEditComponent implements OnInit {

  public formControl: FormControl;
  public type: boolean;
  public isValid: boolean;

  constructor() {
    this.formControl = new FormControl('',
        [Validators.required]);
    this.type = true;
    this.isValid = false;
  }

  get valid(): boolean {
    if (this.type) {
      return false;
    }
    return this.formControl.invalid || !this.isValid;
  }

  ngOnInit(): void {}

  public typeChange(value: boolean): void {
    this.type = value;
  }

  public title(type: string): string {
    return `${L10nStatic.translate(type)}`;
  }

  public onInput(target): void {
    this.isValid = (/^(?=.*\d)\d*(?:\.\d{0,3})?$/g).test(target.value) && +target.value >= 0 && +target.value <= 100;
  }
}
