export class AudienceResearch {
    public id: number;
    public status: string;
    public startDate: string;
    public targetChannel: Channel;
    public channels: Array<AudienceResearchChannel>;
    public advertisers: Array<Account>;
    public version: number;

    constructor() {
        this.targetChannel = new Channel();
        this.channels = [];
        this.advertisers = [];
    }
}

export class AudienceResearchChannel {
    public id: number;
    public status: string;
    public channel: Channel;
    public chartType: string;
    public startDate: string;
    public sortOrder: number;
    public yesterdayComment: string;
    public totalComment: string;
    public version: number;

    constructor() {
        this.channel = new Channel();
        this.chartType = 'BAR_VERTICAL';
    }
}

export class Channel {
    public id: number;
    public name: string;
    public majorDisplayStatus: string;
    public account: Account;
}

export class Account {
    public id: number;
    public name: string;
    public majorDisplayStatus: string;
}