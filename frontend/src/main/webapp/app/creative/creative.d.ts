export interface Creative {
    'id':           number,
    'accountId':    number,
    'agencyId':     number,
    'sizeId':       number,
    'templateId':   number,
    'width':        number,
    'height':       number,
    'version':      number,
    'name':             string,
    'sizeName':         string,
    'templateName':     string,
    'displayStatus':    string,
    'expansion':        string,
    'options':              Array<CreativeOption>,
    'contentCategories':    Array<CreativeContentCategory>,
    'visualCategories':     Array<any>,
    'expandable': boolean
}

export interface CreativeOption {
    'id':       number,
    'token':    string,
    'value':    string
}

export interface CreativeContentCategory {
    'id':       number,
    'name':     string
}