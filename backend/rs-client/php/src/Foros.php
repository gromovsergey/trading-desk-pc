<?php

require_once('service/CampaignService.php');
require_once('service/CampaignCreativeGroupService.php');
require_once('service/CreativeService.php');
require_once('service/CreativeLinkService.php');
require_once('service/AdvertisingChannelService.php');
require_once('service/DiscoverChannelService.php');
require_once('service/CCGKeywordService.php');
require_once('service/SiteCreativeApprovalService.php');
require_once('service/TriggerQAService.php');
require_once('service/ThirdPartyCreativeService.php');
require_once('service/ReportService.php');
require_once('rsclient/RsConfiguration.php');


class Foros {

    private $configuration;

    private function __construct(RsConfiguration $configuration) {
        $this->configuration = $configuration;
    }

    public static function create($baseUrl, $token, $key, $proxy = null) {
        $configuration = new RsConfiguration($baseUrl.'/rs', new RsSecurityInfo($token, $key), $proxy);
        return new Foros($configuration);
    }

    
    public function getCampaignService() {
	    return new CampaignService($this->configuration);
    }
    
    public function getCampaignCreativeGroupService() {
	    return new CampaignCreativeGroupService($this->configuration);
    }
    
    public function getCreativeService() {
	    return new CreativeService($this->configuration);
    }
    
    public function getCreativeLinkService() {
	    return new CreativeLinkService($this->configuration);
    }
    
    public function getAdvertisingChannelService() {
	    return new AdvertisingChannelService($this->configuration);
    }
    
    public function getDiscoverChannelService() {
	    return new DiscoverChannelService($this->configuration);
    }
    
    public function getCCGKeywordService() {
	    return new CCGKeywordService($this->configuration);
    }
    
    public function getSiteCreativeApprovalService() {
	    return new SiteCreativeApprovalService($this->configuration);
    }
    
    public function getTriggerQAService() {
	    return new TriggerQAService($this->configuration);
    }
    
    public function getThirdPartyCreativeService() {
	    return new ThirdPartyCreativeService($this->configuration);
    }
    

    public function getReportService() {
        return new ReportService($this->configuration);
    }
}
