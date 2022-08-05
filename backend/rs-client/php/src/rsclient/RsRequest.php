<?php

class RsRequest {

    private $handle;
    private $response;

    function __construct($method, $url, $securityInfo, array $parameters = null, $data = null, $proxy = null, $file = null) {
        $this->handle = curl_init();

        if ($proxy) {
            curl_setopt($this->handle, CURLOPT_PROXY, $proxy);
        }

        if ($parameters) {
            $url .= $this->buildQuery($parameters);
        }

        curl_setopt($this->handle, CURLOPT_URL, $url);
        curl_setopt($this->handle, CURLOPT_HEADER, 0);

        $this->response = new RsResponse($this->handle);

        if ($file) {
            curl_setopt($this->handle, CURLOPT_FILE, $file);
            curl_setopt($this->handle, CURLOPT_HEADER, 0);
        } else {
            curl_setopt($this->handle, CURLOPT_WRITEFUNCTION, array($this->response, "_bodyHandler"));
        }
        curl_setopt($this->handle, CURLOPT_HEADERFUNCTION, array($this->response, "_headerHandler"));

        $headers = array();

        switch ($method) {
            case "GET":
                curl_setopt($this->handle, CURLOPT_HTTPGET, TRUE);
                break;
            case "POST":
                curl_setopt($this->handle, CURLOPT_POST, TRUE);
                $headers[] = "Content-Type: application/xml";
                break;
        }

        if ($data) {
            curl_setopt($this->handle, CURLOPT_POSTFIELDS, $data);
        }

        if ($securityInfo) {
            foreach ($securityInfo->getHeaders() as $headerName => $headerValue) {
                $headers[] = $headerName . ": " . $headerValue;
            }
        }

        curl_setopt($this->handle, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($this->handle, CURLOPT_FOLLOWLOCATION, FALSE);
    }

    public function send() {
        curl_exec($this->handle);
        return $this->response->prepare();
    }

    private function buildQuery($parameters) {
        $result = "?";
        
        foreach ($parameters as $parameterName => $parameterValue) {
            if (is_array($parameterValue)) {
                foreach ($parameterValue as $value) {
                    if ($value !== null) {
                        $result .= $parameterName . "=" . $value . "&";
                    }
                }
            } else {
                if ($parameterValue !== null) {
                    $result .= $parameterName . "=" . $parameterValue. "&";
                }
            }
        }

        return urldecode($result);
    }

}
