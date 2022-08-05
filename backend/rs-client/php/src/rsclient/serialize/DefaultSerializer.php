<?php

require_once "DataSerializer.php";

class DefaultSerializer implements XmlSerializer {

    function serialize($object) {
        return $object;
    }

}
