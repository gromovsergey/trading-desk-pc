<?php

/**
 
 */
class TriggerQAService {

    private $configuration;

    public function __construct(RsConfiguration $configuration) {
        $this->configuration = $configuration;
    }

    
    public function get (array $triggerQASelector) {
        $client = $this->configuration->createClient();
        $parameters = null;
        $data = null;
    
        $parameters = $triggerQASelector;
        
    
        return $client
                ->request("GET" ,"/channels/triggerQA/", $parameters, $data);
    }
    
    public function perform ($operations) {
        $client = $this->configuration->createClient();
        $parameters = null;
        $data = null;
    
        
        $data = $operations;
    
        return $client
                ->request("POST" ,"/channels/triggerQA/", $parameters, $data);
    }
}
