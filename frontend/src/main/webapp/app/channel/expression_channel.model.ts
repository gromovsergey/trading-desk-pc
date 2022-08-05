export class ExpressionChannel {
    public id: number;
    public name: string;
    public country: string;
    public accountId: number;
    public visibility: string;
    public version: number;

    public includedChannels: Array<Array<any>>;
    public excludedChannels: Array<Array<any>>;

    constructor() {
        this.includedChannels = [];
        this.excludedChannels = [];
    }
}