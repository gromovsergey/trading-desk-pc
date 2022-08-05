<?php

/**
 
 */
class CreativeLinkService {

    private $configuration;

    public function __construct(RsConfiguration $configuration) {
        $this->configuration = $configuration;
    }

    
    public function get (array $creativeLinkSelector) {
        $client = $this->configuration->createClient();
        $parameters = null;
        $data = null;
    
        $parameters = $creativeLinkSelector;
        
    
        return $client
                ->request("GET" ,"/creativeLinks/", $parameters, $data);
    }
    
    public function perform ($creativeLinkOperations) {
        $client = $this->configuration->createClient();
        $parameters = null;
        $data = null;
    
        
        $data = $creativeLinkOperations;
    
        return $client
                ->request("POST" ,"/creativeLinks/", $parameters, $data);
    }
}
