import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {MatTabChangeEvent} from "@angular/material/tabs";
import {ITabSettings} from "../site-targeting-edit/site-targeting-edit.component";
import {FlightEditDevicesComponent} from "../../flight-edit/flight-edit-devices/flight-edit-devices.component";
import {FlightModel} from "../../../models/flight.model";
import {Action} from "../flight-lineitems.component";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {LineItemService} from "../../../../lineitem/services/lineitem.service";
import {Subject} from "rxjs";
import {takeUntil} from "rxjs/operators";

@Component({
  selector: 'ui-device-edit',
  templateUrl: './device-edit.component.html',
  styleUrls: ['./device-edit.component.scss']
})
export class DeviceEditComponent implements OnInit, OnDestroy {

  public tabSettings: ITabSettings[];
  public activeItem: ITabSettings;
  public animationDuration: number;
  public operationResult: 'info' | 'danger' | 'warning' | 'success';
  public operationText: string;
  public operationDone: boolean;
  public component: Action;
  public selectedDevise: number[];
  public disabled: boolean;
  protected flight: FlightModel;
  private unSubscribe$: Subject<boolean>;

  constructor(
      public dialogRef: MatDialogRef<DeviceEditComponent>,
      private lineItemService: LineItemService,
      @Inject(MAT_DIALOG_DATA) public data) {
    this.flight = new FlightModel();
    this.animationDuration = 1000;
    this.operationDone = false;
    this.disabled = false;
    this.operationText = '';
    this.selectedDevise = [];
    this.unSubscribe$ = new Subject<boolean>();
  }

  ngOnInit(): void {
    this.component = this.editDevicesAction(this.flight);
    this.activeItem = { english: 'Set To', rus: 'Установить', value: 'SET' };
    this.tabSettings = [
      { english: 'Set To', rus: 'Установить', value: 'SET'},
      { english: 'Add Device', rus: 'Добавить', value: 'ADD'},
      { english: 'Remove Device', rus: 'Удалить', value: 'DELETE'}
    ];
  }

  ngOnDestroy(): void {
    this.unSubscribe$.next(true);
    this.unSubscribe$.complete();
  }

  get language(): string {
    return localStorage.getItem('lang');
  }

  public setActiveItem(event: MatTabChangeEvent): void {
    let fieldForFiltering = this.language === 'ru' ? 'rus' : 'english';
    this.activeItem = this.tabSettings.filter(tab => tab[fieldForFiltering] === event.tab.textLabel)[0];
    this.destroyInfoPanel();
  }

  public editDevicesAction(flight): Action {
    return {
      component: FlightEditDevicesComponent,
      initializer: (instance: FlightEditDevicesComponent) => {
        return new Promise(resolve => {
          instance.flight = flight;
          instance.deviceChange.pipe(takeUntil(this.unSubscribe$)).subscribe(newDevice => {
            this.selectedDevise = newDevice;
          });
          resolve(true);
        });
      }
    };
  }

  public destroyInfoPanel(): void {
    this.operationDone = false;
  }

  public onClose(): void {
    this.dialogRef.close();
  }

  public onSubmit(): void {
    this.disabled = true;

    this.lineItemService.changeDevise$(this.data, this.selectedDevise, this.activeItem.value, 'DEVICE')
        .pipe(takeUntil(this.unSubscribe$)).subscribe({
      next: (response) => {},
      error: (error) => {
        this.operationResult = 'danger';
        this.operationText = 'Something went wrong. Please try again!';
        this.operationDone = true;
        this.disabled = false;
      },
      complete: () => {
        this.operationResult = 'success';
        this.operationText = 'Operation completed successfully!';
        this.operationDone = true;
        this.disabled = false;
      }
    })
  }
}
