import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'ui-cell',
  styleUrls: ['./cell.component.scss'],
  templateUrl: './cell.component.html'
})
export class CellComponent implements OnInit {

  @Input() title: string;

  ngOnInit(): void {
  }
}
