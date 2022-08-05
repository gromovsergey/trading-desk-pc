<?php
require_once "DataDeserializer.php";

class DefaultDeserializer implements DataDeserializer {

    function deserialize($data) {
        return $data;
    }
}
