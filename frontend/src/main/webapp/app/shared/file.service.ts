import {Injectable} from '@angular/core';
import {Http}       from '@angular/http';
import {Router}     from '@angular/router';

import {CommonService} from '../common/common.service';


@Injectable()
export class FileService extends CommonService{

    constructor(public router: Router, public http: Http){
        super(router, http);
    }

    public creativeUpload(file: any, accountId: number): Promise<any> {
        return this.httpPost(this.api.creative.upload, file, {
            accountId: accountId
        });
    }

    public uploadCreativesZip(file: any, accountId: number): Promise<Array<any>> {
        return this.httpPost(this.api.creative.uploadZip, file, {
            accountId: accountId
        });
    }

    public checkFileExist(file: any, accountId: number): Promise<any> {
        return this.httpPost(this.api.creative.checkFileExist, file, {
            accountId: accountId
        });
    }

    public attachmentUpload(file: any, flightId: number): Promise<any> {
        return this.httpPost(this.api.flight.attachmentUpload, file, {
            flightId: flightId
        });
    }

    public getDocuments(accountId: number): Promise<any> {
        return this.httpGet(this.api.account.documents, {
            accountId: accountId
        });
    }

    public checkDocuments(accountId: number): Promise<any> {
        return this.httpGet(this.api.account.checkDocuments, {
            accountId: accountId
        });
    }

    public downloadDocuments(accountId: number, name: string): Promise<any> {
        return this.httpDownload('GET', this.api.account.documentDownload, {}, {
            accountId: accountId,
            name: name
        });
    }

    public deleteDocuments(accountId: number, name: string): Promise<any> {
        return this.httpDelete(this.api.account.documentDelete, {}, {
            accountId: accountId,
            name: name
        });
    }

    public documentUpload(file: any, accountId: number): Promise<any> {
        return this.httpPost(this.api.account.documentUpload, file, {
            accountId: accountId
        });
    }

    public channelReportUpload(file: any, accountId: number): Promise<any> {
        return this.httpPost(this.api.channel.reportUpload, file, {
            accountId: accountId
        });
    }

    public channelReportDownload(accountId: number, name: string): Promise<any> {
        if (accountId != null) {
            return this.httpDownload('GET', this.api.channel.reportDownload, {}, {
                name: name,
                accountId: accountId
            });
        } else {
            return this.httpDownload('GET', this.api.channel.reportDownload, {}, {
                name: name
            });
        }
    }

    public getChannelReportList(accountId: number): Promise<any> {
        return this.httpGet(this.api.channel.reportList, {
            accountId: accountId
        });
    }
}