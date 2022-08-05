<?php

require_once "DataSerializer.php";

class DomXmlSerializer implements XmlSerializer {

    function serialize($array) {
        if(is_a($array, "SimpleXmlElement")) {
            return $array.asXML();
        }

        throw new Exception("DomXmlSerializer do not support this type, olny SimpleXmlElement");
    }

}
