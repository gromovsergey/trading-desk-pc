import {Component, Inject, OnInit} from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";

@Component({
  selector: 'ui-show-info',
  templateUrl: './show-info.component.html',
  styleUrls: ['./show-info.component.scss']
})
export class ShowInfoComponent implements OnInit {
  constructor(public dialogRef: MatDialogRef<ShowInfoComponent>,
              @Inject(MAT_DIALOG_DATA) public message: { text: string, type: 'info' | 'error' | 'warning' | 'check_circle'; }) {}

  ngOnInit(): void {}

  public onClose(): void {
    this.dialogRef.close(true);
  }
}
