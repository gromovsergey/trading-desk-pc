export class CreativeModel implements Creative {
  id: number;
  accountId: number;
  agencyId: number;
  sizeId: number;
  templateId: number;
  width = 0;
  height = 0;
  version: number;
  name = '';
  sizeName: string;
  templateName: string;
  displayStatus = 'LIVE';
  expansion: string;
  options: CreativeOption[] = [];
  contentCategories: CreativeContentCategory[] = [];
  visualCategories: any[] = [];
  expandable = false;
}
