<?php

/**
 
 */
class CampaignCreativeGroupService {

    private $configuration;

    public function __construct(RsConfiguration $configuration) {
        $this->configuration = $configuration;
    }

    
    public function get (array $groupSelector) {
        $client = $this->configuration->createClient();
        $parameters = null;
        $data = null;
    
        $parameters = $groupSelector;
        
    
        return $client
                ->request("GET" ,"/creativeGroups/", $parameters, $data);
    }
    
    public function perform ($campaignCreativeGroupOperations) {
        $client = $this->configuration->createClient();
        $parameters = null;
        $data = null;
    
        
        $data = $campaignCreativeGroupOperations;
    
        return $client
                ->request("POST" ,"/creativeGroups/", $parameters, $data);
    }
}
