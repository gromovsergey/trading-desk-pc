export interface CountryCode {
    'name':    string,
    'code':    string,
}

export interface GeoPlace {
    'id':       number,
    'name':     string,
}

export interface GeoAddress {
    'id': number;
    'address': string;
    'latitude': number;
    'longitude': number;
    'radius': number;
    'radiusUnits': string;
}