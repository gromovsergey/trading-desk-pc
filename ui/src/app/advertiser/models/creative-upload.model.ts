export class CreativeUpload {
  accountId: number;
  altText: string;
  clickUrl: string;
  landingPageUrl: string;
  crAdvTrackPixel?: string
  crAdvTrackPixel2?: string
  categories: number[] = [];
  imagesList: CreativeImage[] = [];
}

export class CreativeImage {
  name: string;
  path: string;
  dimensions: Dimensions = new Dimensions();
  checked: boolean;
  sizeExist: boolean;
}

export class Dimensions {
  width: number;
  height: number;
}
