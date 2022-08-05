import {ConversionModel} from "./conversion.model";
export class ConversionContainerModel {
    public conversion: ConversionModel;
    public displayStatus: string;

    constructor() {
        this.conversion = new ConversionModel();
    }
}