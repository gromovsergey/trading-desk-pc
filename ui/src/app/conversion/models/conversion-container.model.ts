import {ConversionModel} from './conversion.model';

export class ConversionContainerModel {
  conversion: ConversionModel;
  displayStatus: string;

  constructor() {
    this.conversion = new ConversionModel();
  }
}
