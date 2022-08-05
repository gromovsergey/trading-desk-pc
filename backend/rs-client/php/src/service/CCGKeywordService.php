<?php

/**
 
 */
class CCGKeywordService {

    private $configuration;

    public function __construct(RsConfiguration $configuration) {
        $this->configuration = $configuration;
    }

    
    public function get (array $ccgKeywordSelector) {
        $client = $this->configuration->createClient();
        $parameters = null;
        $data = null;
    
        $parameters = $ccgKeywordSelector;
        
    
        return $client
                ->request("GET" ,"/keywords/", $parameters, $data);
    }
    
    public function perform ($ccgKeywordOperations) {
        $client = $this->configuration->createClient();
        $parameters = null;
        $data = null;
    
        
        $data = $ccgKeywordOperations;
    
        return $client
                ->request("POST" ,"/keywords/", $parameters, $data);
    }
}
