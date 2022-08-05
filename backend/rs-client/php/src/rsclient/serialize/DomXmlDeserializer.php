<?php

require_once "DataDeserializer.php";

class DomDataDeserializer implements DataDeserializer {

    function deserialize($data) {
        return new SimpleXMLElement($data);
    }

}
