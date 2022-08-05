<?php

/**
 
 */
class ThirdPartyCreativeService {

    private $configuration;

    public function __construct(RsConfiguration $configuration) {
        $this->configuration = $configuration;
    }

    
    public function get (array $thirdPartyCreativeSelector) {
        $client = $this->configuration->createClient();
        $parameters = null;
        $data = null;
    
        $parameters = $thirdPartyCreativeSelector;
        
    
        return $client
                ->request("GET" ,"/thirdPartyCreatives/", $parameters, $data);
    }
    
    public function perform ($operations) {
        $client = $this->configuration->createClient();
        $parameters = null;
        $data = null;
    
        
        $data = $operations;
    
        return $client
                ->request("POST" ,"/thirdPartyCreatives/", $parameters, $data);
    }
}
