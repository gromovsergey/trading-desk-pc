import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {HttpClient, HttpParams} from '@angular/common/http';
import {UserSessionModel} from '../../user/models/user-session.model';
import {environment} from 'src/environments/environment';
import {API} from '../../const';
import {Observable, of} from "rxjs";

@Injectable()
export class CommonService {

  api = API;
  host = environment.host;

  constructor(public router: Router, public http: HttpClient) {
  }

  getUrlParams(params: Array<{ key: string; val: any }>): HttpParams {
    const searchParams = new HttpParams();
    if (params && params.length) {
      params.forEach(v => {
        searchParams.append(v.key, v.val);
      });
    }
    return searchParams;
  }

  filterParams(params: any): any {
    const res = {};
    if (params) {
      Object.entries(params).forEach(param => {
        if (param[1] !== null && param[1] !== undefined) {
          res[param[0]] = `${param[1]}`;
        }
      });
      return res;
    } else {
      return null;
    }
  }

  httpGet(url: string, params?: HttpParams | any): Promise<any> {
    return this.http.get(this.host + url, {params: this.filterParams(params)}).toPromise();
  }

  httpGet$(url: string, params?: HttpParams | any): Observable<any> {
    return this.http.get(this.host + url, {params: this.filterParams(params)});
  }

  httpPut(url: string, body?: any, params?: HttpParams | any): Promise<any> {
    return this.http.put(this.host + url, body, {params: this.filterParams(params)}).toPromise();
  }

  httpPut$(url: string, body?: any, params?: HttpParams | any): Observable<any> {
    return this.http.put(this.host + url, body, {params: this.filterParams(params)});
  }

  httpPost(url: string, body?: any, params?: HttpParams | any): Promise<any> {
    return this.http.post(this.host + url, body, {params: this.filterParams(params)}).toPromise();
  }

  httpPost$(url: string, body?: any, params?: HttpParams | any): Observable<any> {
    return this.http.post(this.host + url, body, {params: this.filterParams(params)});
  }

  httpDelete(url: string, params?: HttpParams | any): Promise<any> {
    return this.http.delete(this.host + url, {params: this.filterParams(params)}).toPromise();
  }

  httpDownload(method: string, url: string, requestData?: unknown, searchParams?: unknown, unsafe?: boolean): Promise<any> {

    return new Promise((resolve, reject) => {
      const fullUrl = (/mockup/.test(url)) ? url : this.host + url;
      const oReq = new XMLHttpRequest();
      const user = new UserSessionModel();

      const serialize = (obj: any) => {
        const str = [];
        for (const p in obj) {
          if (obj.hasOwnProperty(p)) {
            str.push(encodeURIComponent(p) + '=' + encodeURIComponent(obj[p]));
          }
        }
        return str.join('&');
      };

      oReq.open(method, fullUrl + (searchParams ? '?' + serialize(searchParams) : ''), true);
      if (!unsafe) {
        oReq.setRequestHeader('Authorization', user.token + ':' + user.key);
      }
      oReq.setRequestHeader('Content-type', 'application/json');
      oReq.responseType = 'blob';

      oReq.onload = (e) => {
        resolve(e);
      };
      oReq.onerror = (e) => {
        reject(e);
      };
      oReq.send(JSON.stringify(requestData));
    });
  }

  isAllowedLocal(entityId: any, name: string): Promise<boolean> {
    return this.http.get<boolean>(this.host + API.restrictionLocal, {
      params: {
        name,
        entityId
      }
    }).toPromise();
  }

  public isAllowedLocal$(name: string): Observable<boolean> {
    return this.http.get<boolean>(this.host + API.restrictionLocal, {
      params: {
        name
      }
    })
  }

  isAllowedLocal0(name: string): Promise<boolean> {
    return this.http.get<boolean>(this.host + API.restrictionLocal, {
      params: {
        name
      }
    }).toPromise();
  }

  isAllowed(entityId: any, name: string): any {
    const isArray = Array.isArray(entityId);
    if (!isArray) {
      entityId = [entityId];
    }
    return this.http.put<any>(this.host + API.restriction, Array.isArray(entityId) ? entityId : [entityId], {
      params: {name}
    }).toPromise().then(res => Promise.resolve(isArray ? res : res.pop()));
  }

  getRequestId(): number {
    return window.crypto ? crypto.getRandomValues(new Uint32Array(1))[0] : Math.trunc(Math.random() * 1e9);
  }

  protected handleError(error: any): any {
    switch (error.status) {
      case 401:
        this.router.navigate(['/login']);
        break;
      case 403:
        this.router.navigate(['/error/403']);
        break;
      case 404:
        this.router.navigate(['/error/404']);
        break;
      case 500:
        this.router.navigate(['/error/500']);
        break;
      case 412:
        break;
      default:
        this.router.navigate(['/error']);
    }
    return Promise.reject(error);
  }
}

// eslint-disable-next-line
export function RequestIdFilter(target: CommonService, propertyKey: string, descriptor: TypedPropertyDescriptor<any>) {
  const originalMethod = descriptor.value;
  const key = '___' + propertyKey + 'RequestId';
  target[key] = null;

  descriptor.value = (...args: any[]) => {
    const requestId = target[key] = target.getRequestId();
    const result: Promise<any> = originalMethod.apply(this, args);
    return result.then(obj => {
      if (requestId !== target[key]) {
        return Promise.reject('Method ' + propertyKey +
          ': request id ' + requestId + ' is wrong (' + target[key] + ' expected)');
      }
      return Promise.resolve(obj);
    });
  };

  return descriptor;
}
