<?php

/**
 
 */
class SiteCreativeApprovalService {

    private $configuration;

    public function __construct(RsConfiguration $configuration) {
        $this->configuration = $configuration;
    }

    
    public function get (array $selector) {
        $client = $this->configuration->createClient();
        $parameters = null;
        $data = null;
    
        $parameters = $selector;
        
    
        return $client
                ->request("GET" ,"/siteCreativeApprovals/", $parameters, $data);
    }
    
    public function perform ($operations) {
        $client = $this->configuration->createClient();
        $parameters = null;
        $data = null;
    
        
        $data = $operations;
    
        return $client
                ->request("POST" ,"/siteCreativeApprovals/", $parameters, $data);
    }
}
