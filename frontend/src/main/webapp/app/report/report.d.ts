export class IdName {
    public id: number;
    public name: string;
}

declare class ReportColumn {
    public id: string;
    public name: string;
    public location: string;
}

export declare class ReportMeta {
    public available: Array<string>;
    public defaults: Array<string>;
    public required: Array<string>;
    public columnsInfo: Array<ReportColumn>;
}

export declare class ReportParameters {
    public accountId: number;
    public dateStart: string;
    public dateEnd: string;
    public selectedColumns: Array<string>;
}

export declare class AdvertiserReportParameters extends ReportParameters {
    public accountId: number;
    public flightIds: Array<number>;
}

export declare class ConversionsReportParameters extends ReportParameters {
    public accountId: number;
    public flightIds: Array<number>;
    public lineItemIds: Array<number>;
    public conversionIds: Array<number>;
}

export declare class DomainsReportParameters extends ReportParameters {
    public accountId: number;
}

export declare class PublisherReportParameters extends ReportParameters {
    public accountId: number;
}

export declare class ReferrerReportParameters extends ReportParameters {
    public accountId: number;
    public tagIds: Array<number>;
}

export declare class DetailedReportParameters extends ReportParameters {
    public advertiserAccountId;
    public publisherAccountId;
}

export declare class SegmentsReportParameters extends ReportParameters {
    public lineItemId;
}

export declare class LineItemIdNameModel {
    public id: number;
    public name: string;
}

export interface ReportService {
    getReportMeta(parameters: ReportParameters): Promise<ReportMeta>;
    generateReport(parameters: ReportParameters): Promise<any>;
    downloadReport(parameters: ReportParameters, format: string): Promise<any>;
}
