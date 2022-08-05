import { Injectable } from '@angular/core';
import { NativeDateAdapter } from "@angular/material/core";

@Injectable({
  providedIn: 'root'
})
export class CustomMatRangeService extends NativeDateAdapter {
  getFirstDayOfWeek(): number {
    return localStorage.getItem('lang') === 'ru' || localStorage.getItem('lang') === null ? 1 : 0;
  }
}
