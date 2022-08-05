<?php
 
class RsResult {

    private $xmlDeserializer;
    private $response;

    public function __construct($xmlDeserializer, $response) {
        $this->xmlDeserializer = $xmlDeserializer;
        $this->response = $response;
    }

    public function asObject() {
        return $this->xmlDeserializer->deserialize($this->response->getData());
    }

    public function asString() {
        return $this->response->getData();
    }

    public function getResponse() {
        return $this->response;
    }

}
