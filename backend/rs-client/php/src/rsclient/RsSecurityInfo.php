<?php

class RsSecurityInfo {

    private $token;
    private $key;

    public function __construct($token, $key) {
        $this->token = $token;
        $this->key = $key;
    }

    public function getHeaders() {
        $timestamp = time() * 1000;

        return array(
            RsHeaders::TIMESTAMP_HEADER => $timestamp,
            RsHeaders::AUTHORIZATION_HEADER => $this->generateAuthorizationString($timestamp)
        );
    }

    private function generateAuthorizationString($timestamp) {
        $signature = base64_encode(hash_hmac("sha512", "$timestamp", base64_decode($this->key), true));
        return "TIMESTAMP $this->token:$signature";
    }

}
