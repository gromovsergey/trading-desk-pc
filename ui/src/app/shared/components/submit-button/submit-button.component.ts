import {Component, Input} from '@angular/core';

@Component({
  selector: 'ui-submit-button',
  styleUrls: ['./submit-button.component.scss'],
  templateUrl: './submit-button.component.html'
})
export class SubmitButtonComponent {

  @Input() disabled: boolean;
  @Input() wait: boolean;
  @Input() title: string;
  @Input() icon: string;

}
