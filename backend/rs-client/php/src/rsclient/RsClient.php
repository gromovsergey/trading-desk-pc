<?php

require_once "RsRequest.php";
require_once "RsResponse.php";
require_once "RsResult.php";
require_once "const/RsHeaders.php";
require_once "serialize/DefaultDeserializer.php";
require_once "serialize/DefaultSerializer.php";

class ErrorUtils {

    private static $PEAR = null;

    private static function pear() {
        if (!self::$PEAR) {
            self::$PEAR = new PEAR();
        }
        return self::$PEAR;
    }

    public static function isError($data) {
        return self::pear()->isError($data);
    }
}

class RsClient {

    private $serializer;
    private $deserializer;

    private $baseUrl;
    private $proxy;
    private $securityInfo;

    public function __construct($baseUrl, $proxy, $securityInfo) {
        $this->PEAR = new PEAR();

        $this->serializer =  new DefaultSerializer();
        $this->deserializer = new DefaultDeserializer();

        $this->baseUrl = $baseUrl;
        $this->proxy = $proxy;
        $this->securityInfo = $securityInfo;
    }

    public function request($method, $url, $parameters = null, $data = null) {
        $fullUrl = $this->baseUrl.$url;
        $p = $this->fetchParameters($parameters);
        $body = $this->serialize($data);
        $file = $this->deserializer instanceof FileDeserializer ? $this->deserializer->getFile() : null;

        $request = new RsRequest($method, $fullUrl, $this->securityInfo, $p, $body, $this->proxy, $file);

        $response = $request->send();

        return new RsResult($this->deserializer, $response);
    }

    public static function hasError(RsResult $rsResult) {
        switch ($rsResult->getResponse()->getCode()) {
            case 204:
                return false;
            case 200:
                return ErrorUtils::isError($rsResult->asObject());
            case 0:
            case 401:
            case 403:
            case 404:
            case 412:
            case 500:
            default :
                return true;
        }
    }

    public static function getErrorMessage(RsResult $rsResult) {
        $errorDescr = "";
        switch ($rsResult->getResponse()->getCode()) {
            case 200: break;
            case 0: $errorDescr .= "ConnectionRefusedError: "; break;
            case 401: $errorDescr .= "AuthenticationError: "; break;
            case 403: $errorDescr .= "NotAuthorizedError: "; break;
            case 404: $errorDescr .= "NotFoundError: "; break;
            case 412: $errorDescr .= "ConstraintViolationError: "; break;
            case 500: $errorDescr .= "UnexpectedError (500): "; break;
            default : $errorDescr .= "UnexpectedError: "; break;
        }

        if (strlen($errorDescr) != 0) {
            return $errorDescr.$rsResult->asString();
        }

        $resultObj = $rsResult->asObject();

        if(ErrorUtils::isError($resultObj)) {
            return $resultObj->getMessage();
        }

        return null;
    }

    public function dataSerializer($serializer) {
        $this->serializer = $serializer;
        return $this;
    }

    public function dataDeserializer($deserializer) {
        $this->deserializer = $deserializer;
        return $this;
    }

    private function fetchParameters($parameters) {
        $nullRef = null;
        if($parameters == null) {
            return $nullRef;
        } else if (is_array($parameters)) {
            return $parameters;
        }

        $c = new ReflectionClass(get_class($parameters));
        if ($c->implementsInterface('ParametersAware')) {
            return $parameters->asParametersArray();
        }

        // todo!!
        return $nullRef;
    }

    private function serialize($data) {
        if (!$data) {
            return null;
        }

        return $this->serializer->serialize($data);
    }

}
