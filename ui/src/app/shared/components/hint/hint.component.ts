import {Component, Input, ViewChild, ElementRef} from '@angular/core';

@Component({
  selector: 'ui-hint',
  templateUrl: './hint.component.html',
  styleUrls: ['./hint.component.scss']
})
export class HintComponent {
  @ViewChild('hint') hintElement: ElementRef;
  @Input() text: string;
}
