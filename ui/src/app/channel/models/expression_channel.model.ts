export class ExpressionChannel {
  id: number;
  name: string;
  country: string;
  accountId: number;
  visibility: string;
  version: number;
  includedChannels: any[][];
  excludedChannels: any[][];

  constructor() {
    this.includedChannels = [];
    this.excludedChannels = [];
  }
}
