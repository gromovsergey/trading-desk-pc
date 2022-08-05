<?php

/**
 
 */
class AdvertisingChannelService {

    private $configuration;

    public function __construct(RsConfiguration $configuration) {
        $this->configuration = $configuration;
    }

    
    public function get (array $channelSelector) {
        $client = $this->configuration->createClient();
        $parameters = null;
        $data = null;
    
        $parameters = $channelSelector;
        
    
        return $client
                ->request("GET" ,"/channels/advertising/", $parameters, $data);
    }
    
    public function perform ($channelOperations) {
        $client = $this->configuration->createClient();
        $parameters = null;
        $data = null;
    
        
        $data = $channelOperations;
    
        return $client
                ->request("POST" ,"/channels/advertising/", $parameters, $data);
    }
}
