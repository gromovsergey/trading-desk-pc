import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {API} from '../../const';
import {environment} from '../../../environments/environment';

export interface IQuickSearch {
  type: string;
  items: {
    id: number;
    type: string;
    name: string;
    displayStatus: string;
  }[]
}

@Injectable()
export class QuickSearchService {

  host = environment.host;

  constructor(private http: HttpClient) {
  }

  public search(searchText: string): Observable<IQuickSearch[]> {
    return this.http.get<IQuickSearch[]>(this.host + API.quick_search, {
      params: {
        quick_search: searchText
      }
    });
  }
}
