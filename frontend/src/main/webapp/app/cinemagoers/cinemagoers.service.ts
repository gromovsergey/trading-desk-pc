import { Injectable } from '@angular/core';
import { CINEMAGOERS_DATA } from './cinemagoers.const';

@Injectable()
export class CinemagoersService {
  public getData(categoryName: string): Promise<any> {
    return Promise.resolve(CINEMAGOERS_DATA[categoryName]);
  }
}
