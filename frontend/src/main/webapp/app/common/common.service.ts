import {Injectable} from '@angular/core';
import {Router}     from '@angular/router';
import {Headers, Http, URLSearchParams, RequestMethod} from '@angular/http';

import {UserSessionModel} from '../user/user_session.model';
import {api}              from './api.conf';

@Injectable()
export class CommonService {

    protected api: any  = api;
    private host        = process.env._JAVA_HOST_;

    constructor(public router: Router, public http: Http) {}

    private getAuthHeaders(): Headers{
        let user    = new UserSessionModel();
        if (!user.isLogged()){
            this.router.navigate(['/login']);
            return null;
        }
        let headers = new Headers();
        headers.append('Authorization', user.token+':'+user.key);
        return headers;
    }

    public getUrlParams(params: Array<{key: string, val: any}>): URLSearchParams{
        let searchParams    = new URLSearchParams();
        if (params && params.length){
            params.forEach(v => {
                searchParams.append(v.key, v.val);
            });
        }
        return searchParams;
    }

    public mapUrlParams(input: {}): Array<{key: string, val: any}>{
        if (input){
            return Object.keys(input).map((k)=>{return {key: k, val: input[k]}});
        } else {
            return null;
        }
    }

    private getRequest(method: RequestMethod, url: string, searchParams: {}, postParams: any, unsafe?:boolean, full?:boolean):Promise<any>{
        let fullUrl = (/mockup/.test(url)) ? url : this.host + url;

        return this.http.request(fullUrl, {
            body: postParams ? postParams : null,
            method: method,
            search: this.getUrlParams(this.mapUrlParams(searchParams)),
            headers: !unsafe ? this.getAuthHeaders() : null
        })
            .toPromise()
            .then(response => {
                if (full) return response;
                return response.text() ? response.json() : {};
            })
            .catch(this.handleError.bind(this));
    }

    public httpGet(url: string, searchParams?: {}, unsafe?:boolean):Promise<any>{
        return this.getRequest(RequestMethod.Get, url, searchParams, null, unsafe);
    }

    public httpPost(url: string, postParams: any, searchParams?: {}, unsafe?:boolean):Promise<any>{
        return this.getRequest(RequestMethod.Post, url, searchParams, postParams, unsafe);
    }

    public httpPut(url: string, putParams: any, searchParams?: {}, unsafe?:boolean, full?:boolean):Promise<any>{
        return this.getRequest(RequestMethod.Put, url, searchParams, putParams, unsafe, full);
    }

    public httpDelete(url: string, deleteParams: any, searchParams?: {}, unsafe?:boolean):Promise<any>{
        return this.getRequest(RequestMethod.Delete, url, searchParams, deleteParams, unsafe);
    }

    public httpDownload(method: string, url: string, requestData?: {}, searchParams?: {}, unsafe?:boolean):Promise<any>{

        return new Promise((resolve, reject) => {
            let fullUrl = (/mockup/.test(url)) ? url : this.host + url;
            let oReq    = new XMLHttpRequest();
            let user    = new UserSessionModel();

            let serialize = function(obj: any) {
                var str = [];
                for(var p in obj)
                    if (obj.hasOwnProperty(p)) {
                        str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                    }
                return str.join("&");
            };

            oReq.open(method, fullUrl + (searchParams ? '?'+serialize(searchParams) : ''), true);
            if (!unsafe){
                oReq.setRequestHeader('Authorization', user.token+':'+user.key);
            }
            oReq.setRequestHeader('Content-type','application/json');
            oReq.responseType = "blob";

            oReq.onload = (e) => {
                resolve(e);
            };
            oReq.onerror    = (e) => {
                reject(e);
            };
            oReq.send(JSON.stringify(requestData));
        });
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

    public isAllowedLocal(entityId: any, name: string): Promise<boolean> {
        return this.httpGet(this.api.restrictionLocal, {
            name: name,
            entityId: entityId
        });
    }

    public isAllowedLocal0(name: string): Promise<boolean> {
        return this.httpGet(this.api.restrictionLocal, {
            name: name
        });
    }

    public isAllowed(entityId: any, name: string){
        let isArray = Array.isArray(entityId);
        if (!isArray){
            entityId    = [entityId];
        }
        return this.httpPut(this.api.restriction, Array.isArray(entityId) ? entityId : [entityId], {
            name: name
        }).then(res => {
            return Promise.resolve(isArray ? res : res.pop());
        });
    }

    public getRequestId(): number {
        return window.crypto ? crypto.getRandomValues(new Uint32Array(1))[0] : Math.trunc(Math.random()*1e9);
    }
}

export function RequestIdFilter(target: CommonService, propertyKey: string, descriptor: TypedPropertyDescriptor<any>) {
    const originalMethod = descriptor.value;
    const key = "___" + propertyKey + "RequestId";
    target[key] = null;

    descriptor.value = function(...args: any[]) {
        var requestId = target[key] = target.getRequestId();

        const result: Promise<any> = originalMethod.apply(this, args);
        return result.then(obj => {
            if (requestId !== target[key]) {
                return Promise.reject('Method ' + propertyKey +
                    ": request id " + requestId + " is wrong (" + target[key] + " expected)");
            }
            return Promise.resolve(obj);
        });
    };

    return descriptor;
}
