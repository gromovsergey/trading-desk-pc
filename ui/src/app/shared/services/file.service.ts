import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {CommonService} from '../../common/services/common.service';
import {API} from '../../const';
import {Observable} from "rxjs";

@Injectable()
export class FileService extends CommonService {

  constructor(public router: Router, public http: HttpClient) {
    super(router, http);
  }

  creativeUpload(file: any, accountId: number): Promise<any> {
    return this.httpPost(API.creative.upload, file, {
      accountId
    });
  }

  uploadCreativesZip(file: any, accountId: number): Promise<Array<any>> {
    return this.httpPost(API.creative.uploadZip, file, {
      accountId
    });
  }

  checkFileExist$(file: any, accountId: number): Observable<boolean> {
    return this.http.post<boolean>(this.host + API.creative.checkFileExist, file, {params: {accountId: accountId.toString()}});
  }

  checkFileExist(file: any, accountId: number): Promise<any> {
    return this.httpPost(API.creative.checkFileExist, file, {
      accountId
    });
  }

  attachmentUpload(file: any, flightId: number): Promise<any> {
    return this.httpPost(API.flight.attachmentUpload, file, {
      flightId
    });
  }

  getDocuments(accountId: number): Promise<any> {
    return this.httpGet(API.account.documents, {
      accountId
    });
  }

  checkDocuments(accountId: number): Promise<any> {
    return this.httpGet(API.account.checkDocuments, {
      accountId
    });
  }

  downloadDocuments(accountId: number, name: string): Promise<any> {
    return this.httpDownload('GET', API.account.documentDownload, {}, {
      accountId,
      name
    });
  }

  deleteDocuments(accountId: number, name: string): Promise<any> {
    return this.httpDelete(API.account.documentDelete, {
      accountId,
      name
    });
  }

  documentUpload(file: any, accountId: number): Promise<any> {
    return this.httpPost(API.account.documentUpload, file, {
      accountId
    });
  }

  channelReportUpload(file: any, accountId: number): Promise<any> {
    return this.httpPost(API.channel.reportUpload, file, {
      accountId
    });
  }

  channelReportDownload(accountId: number, name: string): Promise<any> {
    if (accountId != null) {
      return this.httpDownload('GET', API.channel.reportDownload, {}, {
        name,
        accountId
      });
    } else {
      return this.httpDownload('GET', API.channel.reportDownload, {}, {
        name
      });
    }
  }

  getChannelReportList(accountId: number): Promise<any> {
    return this.httpGet(API.channel.reportList, {
      accountId
    });
  }
}
