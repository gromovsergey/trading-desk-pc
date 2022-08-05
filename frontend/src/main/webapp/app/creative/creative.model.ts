import {Creative, CreativeOption, CreativeContentCategory} from './creative';

export class CreativeModel implements Creative {
    public id:           number;
    public accountId:    number;
    public agencyId:     number;
    public sizeId:       number;
    public templateId:   number;
    public width:        number = 0;
    public height:       number = 0;
    public version:      number;
    public name:                string = '';
    public sizeName:            string;
    public templateName:        string;
    public displayStatus:       string = 'LIVE';
    public expansion:           string;
    public options:              Array<CreativeOption>  = [];
    public contentCategories:    Array<CreativeContentCategory> = [];
    public visualCategories:     Array<any> = [];
    public expandable: boolean  = false;
}