<?php

/**
 
 */
class CampaignService {

    private $configuration;

    public function __construct(RsConfiguration $configuration) {
        $this->configuration = $configuration;
    }

    
    public function get (array $campaignSelector) {
        $client = $this->configuration->createClient();
        $parameters = null;
        $data = null;
    
        $parameters = $campaignSelector;
        
    
        return $client
                ->request("GET" ,"/campaigns/", $parameters, $data);
    }
    
    public function perform ($campaignOperations) {
        $client = $this->configuration->createClient();
        $parameters = null;
        $data = null;
    
        
        $data = $campaignOperations;
    
        return $client
                ->request("POST" ,"/campaigns/", $parameters, $data);
    }
}
