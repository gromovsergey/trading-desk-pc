<?php

require_once("rsclient/serialize/DataDeserializer.php");

class FileDeserializer implements DataDeserializer {

    private $file;

    function __construct($file) {
        $this->file = $file;
    }

    function deserialize($data) {
        // this deserializer deserialize nothing just let curl save body to file
        return $this->file;
    }

    public function getFile() {
        return $this->file;
    }
}
