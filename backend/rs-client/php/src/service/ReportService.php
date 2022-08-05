<?php

require_once("rsclient/serialize/FileDeserializer.php");

class ReportService {

    private $configuration;

    public function __construct(RsConfiguration $configuration) {
        $this->configuration = $configuration;
    }

    public function processTextAdvertisingReport ($parameters, $format, $file) {
        $client = $this
            ->configuration
            ->createClient()
            ->dataDeserializer(new FileDeserializer($file));

        return $client->request("POST" ,"/reporting/textAdvertising", array("format" => $format), $parameters);
    }

    public function processDisplayAdvertisingReport ($parameters, $format, $file) {
        $client = $this
            ->configuration
            ->createClient()
            ->dataDeserializer(new FileDeserializer($file));

        return $client->request("POST" ,"/reporting/displayAdvertising", array("format" => $format), $parameters);
    }

    public function processReferrerReport ($parameters, $format, $file) {
        $client = $this
            ->configuration
            ->createClient()
            ->dataDeserializer(new FileDeserializer($file));

        return $client->request("POST" ,"/reporting/referrer", array("format" => $format), $parameters);
    }
}
