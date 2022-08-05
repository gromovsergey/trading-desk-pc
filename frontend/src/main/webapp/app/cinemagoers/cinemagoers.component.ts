import { Component } from '@angular/core';
import { NgFor } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CinemagoersService } from './cinemagoers.service';
import { CINEMAGOERS_CATEGORIES } from './cinemagoers.const';
import { splitArray } from '../common/utilities';

@Component({
  selector: 'ui-cinemagoers',
  templateUrl: 'cinemagoers.component.html',
})
export class CinemagoersComponent {
  public headline = 'СЕГМЕНТЫ АУДИТОРИИ';
  public categories = splitArray(CINEMAGOERS_CATEGORIES, 3);
}
