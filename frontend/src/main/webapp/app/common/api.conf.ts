export const api = {
    agency: {
        search: "/rest/account/advertising/search",
        get: "/rest/account/advertising",
        availableBudget: "/rest/account/advertising/availableBudget",
        advertisers: "/rest/account/advertising/searchInAgency",
        searchParams: "/rest/account/advertising/search/params"
    },
    advertiser: {
        get: "/rest/account/advertising",
        availableBudget: "/rest/account/advertising/availableBudget",
        add: "/rest/account/advertising",
        update: "/rest/account/advertising",
        updateStatus: "/rest/account/advertising/status",
        flights: "/rest/flight/stat/list",
        flightsList: "/rest/flight/list",
        conversions: "/rest/conversion/stat",
        lineItemsIdNameList: "/rest/lineItem/list",
        site: "/rest/site",
        templates: "/rest/creative/template/stat",
        sizes: "/rest/creative/size/stat",
        imageTemplate: "/rest/creative/imageTemplate",
        uploadCreatives: "/rest/creative/upload"
    },
    account: {
        documents: "/rest/account/documentsList",
        checkDocuments: "/rest/account/checkDocuments",
        documentUpload: "/rest/account/uploadDocument",
        documentDelete: "/rest/account/document",
        documentDownload: "/rest/account/downloadDocument"
    },
    publisher: {
        get: "/rest/account/publisher",
        list: "/rest/account/publisher/list",
        listForReferrerReport: "/rest/account/publisher/list/referrer"
    },
    report: {
        meta: {
            advertiser: "/rest/report/advertiser/meta",
            conversions: "/rest/report/conversions/meta",
            domains: "/rest/report/domains/meta",
            publisher: "/rest/report/publisher/meta",
            referrer: "/rest/report/referrer/meta",
            detailed: "/rest/report/detailed/meta",
            segments: "/rest/report/segments/meta"
        },
        run: {
            advertiser: "/rest/report/advertiser/run",
            conversions: "/rest/report/conversions/run",
            domains: "/rest/report/domains/run",
            publisher: "/rest/report/publisher/run",
            referrer: "/rest/report/referrer/run",
            detailed: "/rest/report/detailed/run",
            segments: "/rest/report/segments/run"
        }
    },
    conversion: {
        get: "/rest/conversion",
        create: "/rest/conversion",
        update: "/rest/conversion",
        updateStatus: "/rest/conversion/operation",
        pixelCode: "/rest/conversion/pixelCode"
    },
    creative: {
        get: "/rest/creative",
        post: "/rest/creative",
        advertiserList: "/rest/creative/stat",
        linkStatus: "/rest/creativeLink/operation",
        preview: "/rest/creative/preview",
        status: "/rest/creative/operation",
        upload: "/rest/creative/fileUpload",
        uploadZip: "/rest/creative/zipUpload",
        checkFileExist: "/rest/creative/checkFileExist",
        categories: "/rest/creative/category/stat",
        options: "/rest/creative/templatesize",
        contentCategories: "/rest/creative/contentCategories",
        livePreview: "/rest/creative/livePreview",
    },
    channel: {
        status: "/rest/channel/operation",
        getBehavioral: "/rest/channel/behavioral",
        getExpression: "/rest/channel/expression",
        createBehavioral: "/rest/channel/behavioral",
        createExpression: "/rest/channel/expression",
        updateBehavioral: "/rest/channel/behavioral",
        updateExpression: "/rest/channel/expression",
        channelOwners: "/rest/account/channelOwners",
        internalAccounts: "/rest/account/internal/list",
        accountChannels: "/rest/channel",
        expressionChannels: "/rest/channel/expression/list",
        externalChannels: "/rest/channel/external/list",
        allChannels: "/rest/channel/search",
        channelSearch: "/rest/channel/searchNames",
        channelRubricNodesSearch: "/rest/channel/rubric/node/list",
        channelNodesSearch: "/rest/channel/node/list",
        getAccount: "/rest/channel/account",
        stats: {
            behavioral: "/rest/channel/behavioral/stat",
            expression: "/rest/channel/expression/stat",
        },
        dynamicLocalizations: "/rest/localization/channel",
        reportUpload: "/rest/channel/uploadChannelReport",
        reportDownload: "/rest/channel/downloadChannelReport",
        reportList: "/rest/channel/channelReportList"
    },
    chart: {
        data: "/rest/flight/chart"
    },
    flight: {
        get: "/rest/flight",
        stats: "/rest/flight/stat",
        status: "/rest/flight/operation",
        add: "/rest/flight",
        update: "/rest/flight",
        site: "/rest/site/stat",
        device: "/rest/device",
        attachments: "/rest/flight/listAttachments",
        attachmentUpload: "/rest/flight/uploadAttachment",
        attachmentDelete: "/rest/flight/attachment",
        attachmentDownload: "/rest/flight/downloadAttachment",
        linkChannels: "/rest/flight/linkAdvertisingChannels",
        channelsStat: "/rest/channel/stat",
        linkSites: "/rest/flight/linkSites",
        creativeList: "/rest/creativeLink/stat",
        creativeLink: "/rest/flight/linkCreatives"
    },
    geo: {
        getLocations: "/rest/geo/location/list",
        getAddresses: "/rest/geo/address/list",
        searchLocation: "/rest/geo/location/search",
        searchAddress: "/rest/geo/address/search"
    },
    lineItem: {
        get: "/rest/lineItem",
        add: "/rest/lineItem",
        stats: "/rest/lineItem/stat",
        status: "/rest/lineItem/operation",
        update: "/rest/lineItem",
        list: "/rest/lineItem/stat/list",
        linkSites: "/rest/lineItem/linkSites",
        site: "/rest/site/stat",
        linkChannels: "/rest/lineItem/linkAdvertisingChannels",
        channelsStat: "/rest/channel/stat",
        creativeList: "/rest/creativeLink/stat",
        creativeLink: "/rest/lineItem/linkCreatives",
        resetableFields: "/rest/lineItem/field/resetAware",
    },
    site: {
        list: "/rest/site/list",
        tagsList: "/rest/tag/list",
    },
    auth: {
        login: "/rest/login",
        logout: "/rest/logout"
    },
    dashboard: {
        dashboardStats: "/rest/dashboard/stat"
    },
    agentReport: {
        totalStats: "/rest/agentreport/total",
        monthlyStats: "/rest/agentreport/monthly",
        monthlyStatsFile: "/rest/agentreport/file",
        closeMonthlyStats: "/rest/agentreport/close"
    },
    audienceResearch: {
        list: "/rest/audienceResearch/list",
        get: "/rest/audienceResearch",
        stat: "/rest/audienceResearch/stat",
        channels: "/rest/audienceResearch/channels",
        advertisers: "/rest/account/advertising/list",
        create: "/rest/audienceResearch",
        update: "/rest/audienceResearch",
        yesterdayComment: "/rest/audienceResearch/yesterdayComment",
        totalComment: "/rest/audienceResearch/totalComment",
        delete: "/rest/audienceResearch"
    },
    quick_search: "/rest/quick_search",
    restriction: "/rest/restriction",
    restrictionLocal: "/rest/restriction/local",
    user: {
        get: "/rest/user",
        getAll: "/rest/user/list",
        status: "/rest/user/operation",
        role: "/rest/user/role",
        profile: "/rest/user/profile",
        advertiser: "/rest/account/advertisers",
        changePassword: "/rest/user/password"
    }
};
