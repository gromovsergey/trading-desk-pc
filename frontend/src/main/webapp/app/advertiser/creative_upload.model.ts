export class CreativeUpload {
    public accountId: number;
    public altText: string;
    public clickUrl: string;
    public landingPageUrl: string;
    public categories: Array<number> = [];
    public imagesList: Array<CreativeImage> = [];
}

export class CreativeImage {
    public name: string;
    public path: string;
    public dimensions: Dimensions = new Dimensions();
    public checked: boolean;
    public sizeExist: boolean;
}

export class Dimensions {
    public width: number;
    public height: number;
}