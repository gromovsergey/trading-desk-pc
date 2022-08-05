import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { L10nFlightRateTypes } from 'src/app/common/L10n.const';
import { FlightModel } from "../../../models/flight.model";
import { ITabSettings } from "../site-targeting-edit/site-targeting-edit.component";
import { Subject } from "rxjs";
import { MatTabChangeEvent } from "@angular/material/tabs";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { LineItemService } from "../../../../lineitem/services/lineitem.service";
import { takeUntil } from "rxjs/operators";

@Component({
  selector: 'ui-rates-edit',
  templateUrl: './rates-edit.component.html',
  styleUrls: ['./rates-edit.component.scss']
})
export class RatesEditComponent implements OnInit, OnDestroy {

  public headerTitle: string;
  public _flight: FlightModel;
  public animationDuration: number;
  public disabled: boolean;
  public operationDone: boolean;
  public operationResult: 'info' | 'danger' | 'warning' | 'success';
  public operationText: string;
  private unSubscribe$: Subject<boolean>;

  L10nFlightRateTypes = L10nFlightRateTypes;

  constructor(
      private dialogRef: MatDialogRef<RatesEditComponent>,
      private lineItemService: LineItemService,
      @Inject(MAT_DIALOG_DATA) public data
  ) {
    this.unSubscribe$ = new Subject<boolean>();
    this._flight = new FlightModel();
    this.animationDuration = 1000;
    this.disabled = false;
    this.operationText = '';
    this.operationDone = false;
    this.headerTitle = 'button.rates'
  }

  ngOnInit(): void {}

  get flight(): FlightModel {
    return this._flight;
  }

  public onClose(): void {
    this.dialogRef.close();
  }

  public destroyInfoPanel(): void {
    this.operationDone = false;
  }

  public onSubmit(): void {
    let rates = {
      rateType:	this.flight.rateType,
      rateValue: this.flight.rateValue
    }

    this.lineItemService.changeRates$(this.data, rates, 'RATES')
        .pipe(takeUntil(this.unSubscribe$))
        .subscribe({
      next: (response) => {},
      error: (error) => {
        this.operationResult = 'danger';
        this.operationText = 'messages.operation.text.wrong';
        this.operationDone = true;
        this.disabled = false;
      },
      complete: () => {
        this.operationResult = 'success';
        this.operationText = 'messages.operation.text.success';
        this.operationDone = true;
        this.disabled = false;
      }
    });
  }

  ngOnDestroy(): void {
    this.unSubscribe$.next(true);
    this.unSubscribe$.unsubscribe();
  }
}
