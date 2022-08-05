<?php

/**
 
 */
class CreativeService {

    private $configuration;

    public function __construct(RsConfiguration $configuration) {
        $this->configuration = $configuration;
    }

    
    public function get (array $creativeSelector) {
        $client = $this->configuration->createClient();
        $parameters = null;
        $data = null;
    
        $parameters = $creativeSelector;
        
    
        return $client
                ->request("GET" ,"/creatives/", $parameters, $data);
    }
    
    public function perform ($creativeOperations) {
        $client = $this->configuration->createClient();
        $parameters = null;
        $data = null;
    
        
        $data = $creativeOperations;
    
        return $client
                ->request("POST" ,"/creatives/", $parameters, $data);
    }
}
