<?php

require_once 'RsSecurityInfo.php';

class RsResponse {

    private $code;

    private $handle;

    private $headers;
    private $data;

    public function __construct($handle) {
        $this->handle = $handle;
    }

    public function _headerHandler($handle, $header) {
        @list($key, $value) = explode(":", rtrim($header), 2);

        if (@!$this->headers[$key]) {
            $this->headers[$key] = array();
        }

        $this->headers[trim($key)][] = trim($value);

        return strlen($header);
    }

    public function _bodyHandler($handle, $data) {
        $this->data .= $data;
        return strlen($data);
    }

    public function prepare() {
        $this->code = curl_getinfo($this->handle, CURLINFO_HTTP_CODE);

        curl_close ($this->handle);

        return $this;
    }

    public function hasData() {
        return $this->data != null && strlen($this->data) > 0;
    }

    public function getData() {
        return $this->data;
    }

    public function getCode() {
        return $this->code;
    }

}
