<?php

require_once "RsClient.php";

class RsConfiguration {

    private $baseUrl;
    private $proxy;
    private $securityInfo;

    public function __construct($baseUrl, $securityInfo, $proxy) {
        $this->baseUrl = $baseUrl;
        $this->securityInfo = $securityInfo;
        $this->proxy = $proxy;
    }

    public function createClient() {
        return new RsClient($this->baseUrl, $this->proxy, $this->securityInfo);
    }

}
