import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'ui-show-warning',
  templateUrl: './show-warning.component.html',
  styleUrls: ['./show-warning.component.scss']
})
export class ShowWarningComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<ShowWarningComponent>,
              @Inject(MAT_DIALOG_DATA) public message: { base: string, optionally: string }) { }

  ngOnInit(): void {}

  public onClose(): void {
    this.dialogRef.close(true);
  }
}
